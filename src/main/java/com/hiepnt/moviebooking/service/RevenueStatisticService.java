package com.hiepnt.moviebooking.service;

import com.hiepnt.moviebooking.dto.response.MovieStatsDTO;
import com.hiepnt.moviebooking.entity.Booking;
import com.hiepnt.moviebooking.entity.FoodBooking;
import com.hiepnt.moviebooking.entity.Movie;
import com.hiepnt.moviebooking.entity.Showtime;
import com.hiepnt.moviebooking.repository.BookingRepository;
import com.hiepnt.moviebooking.repository.MovieRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RevenueStatisticService {
    @PersistenceContext
    EntityManager entityManager;

    MovieRepository movieRepository;

    BookingRepository bookingRepository;

    public Map<LocalDate, BigDecimal> getDailyRevenue(Integer month, Integer year, Integer theaterId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

        Root<Booking> root = query.from(Booking.class);
        Expression<LocalDate> bookingDate = root.get("bookingDate").as(LocalDate.class);
        Expression<BigDecimal> totalPrice = root.get("totalPrice").as(BigDecimal.class);

        Predicate monthCondition = cb.equal(cb.function("MONTH", Integer.class, bookingDate), month);
        Predicate yearCondition = cb.equal(cb.function("YEAR", Integer.class, bookingDate), year);

        Predicate finalCondition = cb.and(monthCondition, yearCondition);

        if (theaterId != null) {
            Predicate theaterCondition = cb.equal(root.get("theaterId"), theaterId);
            finalCondition = cb.and(finalCondition, theaterCondition);
        }

        query.multiselect(bookingDate, cb.sum(totalPrice))
                .where(finalCondition)
                .groupBy(bookingDate)
                .orderBy(cb.asc(bookingDate));

        List<Object[]> results = entityManager.createQuery(query).getResultList();

        Map<LocalDate, BigDecimal> dailyRevenue = new LinkedHashMap<>();
        for (Object[] result : results) {
            dailyRevenue.put((LocalDate) result[0], (BigDecimal) result[1]);
        }
        return dailyRevenue;
    }

    public Map<Integer, BigDecimal> getMonthlyRevenue(Integer year, Integer theaterId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

        Root<Booking> root = query.from(Booking.class);
        Expression<Integer> month = cb.function("MONTH", Integer.class, root.get("bookingDate"));
        Expression<BigDecimal> totalPrice = root.get("totalPrice").as(BigDecimal.class);

        Predicate yearCondition = cb.equal(cb.function("YEAR", Integer.class, root.get("bookingDate")), year);

        Predicate finalCondition = yearCondition;

        if (theaterId != null) {
            Predicate theaterCondition = cb.equal(root.get("theaterId"), theaterId);
            finalCondition = cb.and(finalCondition, theaterCondition);
        }

        query.multiselect(month, cb.sum(totalPrice))
                .where(finalCondition)
                .groupBy(month)
                .orderBy(cb.asc(month));

        List<Object[]> results = entityManager.createQuery(query).getResultList();

        Map<Integer, BigDecimal> monthlyRevenue = new LinkedHashMap<>();
        for (Object[] result : results) {
            monthlyRevenue.put((Integer) result[0], (BigDecimal) result[1]);
        }
        return monthlyRevenue;
    }

    public Map<Integer, BigDecimal> getYearlyRevenue(Integer theaterId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

        Root<Booking> root = query.from(Booking.class);
        Expression<Integer> year = cb.function("YEAR", Integer.class, root.get("bookingDate"));
        Expression<BigDecimal> totalPrice = root.get("totalPrice").as(BigDecimal.class);

        Predicate finalCondition = cb.conjunction();

        if (theaterId != null) {
            Predicate theaterCondition = cb.equal(root.get("theaterId"), theaterId);
            finalCondition = cb.and(finalCondition, theaterCondition);
        }

        query.multiselect(year, cb.sum(totalPrice))
                .where(finalCondition)
                .groupBy(year)
                .orderBy(cb.asc(year));

        List<Object[]> results = entityManager.createQuery(query).getResultList();

        Map<Integer, BigDecimal> yearlyRevenue = new LinkedHashMap<>();
        for (Object[] result : results) {
            yearlyRevenue.put((Integer) result[0], (BigDecimal) result[1]);
        }
        return yearlyRevenue;
    }
    @Transactional
    // Lấy thông tin thống kê của các phim đang chiếu
    public List<MovieStatsDTO> getMovieStats(Integer theaterId) {
        // Lấy danh sách các phim đang chiếu (isActive = true)
        List<Movie> movies = movieRepository.findByIsActiveTrue();
        List<MovieStatsDTO> stats = new ArrayList<>();

        for (Movie movie : movies) {
            double revenue = 0;
            int showtimeCount = 0;
            int ticketSold = 0;
            List<Showtime> showtimes = movie.getShowTimes();
            if(theaterId!=null){
                showtimes = showtimes.stream()
                        .filter(showtime -> Objects.equals(showtime.getRoom().getTheater().getId(), theaterId))
                        .toList();
            }
            for (Showtime showtime : showtimes) {
                // Lấy tất cả các booking cho showtime này
                List<Booking> bookings = showtime.getBookings();

                // Tính doanh thu và số vé đã bán cho mỗi booking
                for (Booking booking : bookings) {
                    revenue += booking.getTotalPrice(); // Thêm tổng tiền hóa đơn

                    // Tính tổng tiền thức ăn
                    double foodTotal = 0;
                    for (FoodBooking food : booking.getFoodBookingList()) {
                        foodTotal += food.getPrice() * food.getQuantity();
                    }
                    revenue -= foodTotal;  // Trừ đi tổng tiền thức ăn

                    // Tính số vé đã bán (theo số ghế đã đặt)
                    ticketSold += booking.getSeatBookingList().size();
                }

                // Tăng số suất chiếu
                showtimeCount++;
            }

            // Thêm thống kê cho phim này
            stats.add(new MovieStatsDTO(movie.getTitle(), revenue, showtimeCount, ticketSold));
        }

        // Sắp xếp theo doanh thu giảm dần
        stats.sort(Comparator.comparingDouble(MovieStatsDTO::getRevenue).reversed());

        return stats;
    }
}