package com.hiepnt.moviebooking.service;

import com.hiepnt.moviebooking.dto.request.ShowtimeCreationDto;
import com.hiepnt.moviebooking.dto.request.ShowtimeUpdateDto;
import com.hiepnt.moviebooking.dto.response.*;
import com.hiepnt.moviebooking.entity.*;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowtimeService {
    RoomRepository roomRepository;
    MovieRepository movieRepository;
    ShowTimeRepository showTimeRepository;
    ShowSeatRepository showSeatRepository;
    DayPriceRepository dayPriceRepository;
    TheaterRepository theaterRepository;
    RoomTypeRepository roomTypeRepository;

    @Transactional
    public SelectAShowTimeResponse getDataWhenSelectAShowtime(int showtimeId){
        Showtime showtime = showTimeRepository.findById(showtimeId)
                .orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        String movieTitle = showtime.getMovie().getTitle();
        String moviePoster = showtime.getMovie().getPoster();
        String roomTypeName = showtime.getRoom().getRoomType().getName();
        String ageRating = showtime.getMovie().getAgeRating();
        String theaterName = showtime.getRoom().getTheater().getName();
        String roomName = showtime.getRoom().getName();
        LocalDate date = showtime.getDate();
        LocalTime timeStart = showtime.getTimeStart();

        List<ShowSeat> showSeatList = showtime.getShowSeats();
        List<ShowSeatResponse> showSeatResponseList= showSeatList.stream().map(showSeat ->
                ShowSeatResponse.builder()
                        .id(showSeat.getId())
                        .showSeatStatus(showSeat.getShowSeatStatus())
                        .seatTypeName(showSeat.getSeatTypeName())
                        .seatRow(showSeat.getSeatRow())
                        .number(showSeat.getNumber())
                        .price(showSeat.getPrice())
                        .build()
        ).toList();

        return SelectAShowTimeResponse.builder()
                .movieTitle(movieTitle)
                .moviePoster(moviePoster)
                .roomTypeName(roomTypeName)
                .ageRating(ageRating)
                .theaterName(theaterName)
                .roomName(roomName)
                .date(date)
                .timeStart(timeStart)
                .showSeatResponseList(showSeatResponseList)
                .build();
    }
    @Transactional
    public List<AvailableDateResponse> getAvailableDatesForMovieFromToday(int movieId) {
        // Lấy danh sách các ngày có suất chiếu
        List<LocalDate> availableDates = showTimeRepository.findDistinctDatesByMovieIdAndFromToday(movieId, LocalDate.now(), LocalTime.now());

        // Format ngày dd/MM
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");

        // Sắp xếp ngày tăng dần và xây dựng danh sách AvailableDateResponse
        return availableDates.stream()
                .sorted() // Sắp xếp theo thứ tự tăng dần
                .map(date -> {
                    String dayOfWeek = date.equals(LocalDate.now()) ? "Hôm nay" : getDayOfWeekName(date.getDayOfWeek());
                    return AvailableDateResponse.builder()
                            .fullDate(date.toString())                // Ngày đầy đủ (yyyy-MM-dd)
                            .date(date.format(dateFormatter))         // Ngày theo định dạng dd/MM
                            .dayOfWeek(dayOfWeek)                    // Thứ (hoặc "Hôm nay")
                            .build();
                })
                .collect(Collectors.toList());
    }


    // Phương thức để lấy tên thứ bằng tiếng Việt
    private String getDayOfWeekName(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "Thứ hai";
            case TUESDAY -> "Thứ ba";
            case WEDNESDAY -> "Thứ tư";
            case THURSDAY -> "Thứ năm";
            case FRIDAY -> "Thứ sáu";
            case SATURDAY -> "Thứ bảy";
            case SUNDAY -> "Chủ nhật";
            default -> "";
        };
    }
    @Transactional
    public List<ShowtimeForUserResponse> getAllByUser(int movieId, LocalDate date) {
        // Bước 1: Lấy danh sách các rạp có suất chiếu của phim vào ngày được chỉ định
        List<Theater> theaters;

        if (date.equals(LocalDate.now())) {
            // Ngày hiện tại: Chỉ lấy các suất chiếu có timeStart >= LocalTime.now()
            theaters = theaterRepository
                    .findDistinctByRooms_ShowTimes_Movie_IdAndRooms_ShowTimes_IsActiveTrueAndRooms_ShowTimes_DateAndRooms_ShowTimes_TimeStartAfter(
                            movieId, date, LocalTime.now());
        } else {
            // Ngày tương lai: Lấy tất cả suất chiếu trong ngày đó
            theaters = theaterRepository
                    .findDistinctByRooms_ShowTimes_Movie_IdAndRooms_ShowTimes_IsActiveTrueAndRooms_ShowTimes_Date(
                            movieId, date);
        }

        // Bước 2: Khởi tạo danh sách ShowtimeForUserResponse để trả về
        List<ShowtimeForUserResponse> showtimeForUserResponses = new ArrayList<>();

        // Bước 3: Lặp qua từng rạp để lấy thông tin chi tiết suất chiếu theo loại phòng
        for (Theater theater : theaters) {
            // Lấy danh sách các loại phòng trong rạp
            List<RoomType> roomTypes;

            if (date.equals(LocalDate.now())) {
                // Ngày hiện tại: Lọc các roomTypes với timeStart >= LocalTime.now()
                roomTypes = roomTypeRepository.findDistinctByRooms_Theater_IdAndRooms_ShowTimes_DateAndRooms_ShowTimes_TimeStartAfter(
                        theater.getId(), date, LocalTime.now());
            } else {
                // Ngày tương lai: Lấy tất cả roomTypes trong ngày đó
                roomTypes = roomTypeRepository.findDistinctByRooms_Theater_IdAndRooms_ShowTimes_Date(
                        theater.getId(), date);
            }


            // Khởi tạo danh sách ShowtimeByRoomType
            List<ShowtimeByRoomType> showtimeByRoomTypeList = new ArrayList<>();

            // Lặp qua từng loại phòng để lấy danh sách suất chiếu
            for (RoomType roomType : roomTypes) {
                // Lấy danh sách suất chiếu theo thời gian cho mỗi loại phòng và ngày
                List<Showtime> showtimes;

                if (date.equals(LocalDate.now())) {
                    // Nếu là ngày hiện tại, chỉ lấy các suất chiếu có timeStart >= LocalTime.now()
                    LocalTime currentTime = LocalTime.now();
                    showtimes = showTimeRepository.findByMovie_IdAndRoom_RoomType_IdAndRoom_Theater_IdAndDateAndTimeStartAfter(
                            movieId, roomType.getId(), theater.getId(), date, currentTime);
                } else {
                    // Nếu là ngày trong tương lai, lấy tất cả suất chiếu trong ngày đó
                    showtimes = showTimeRepository.findByMovie_IdAndRoom_RoomType_IdAndRoom_Theater_IdAndDate(
                            movieId, roomType.getId(), theater.getId(), date);
                }

                // Khởi tạo danh sách ShowtimeByTimeResponse
                List<ShowtimeByTimeResponse> showtimeByTimeResponseList = showtimes.stream()
                        .map(showtime -> ShowtimeByTimeResponse.builder()
                                .id(showtime.getId())
                                .timeStart(showtime.getTimeStart())
                                .build())
                        .collect(Collectors.toList());

                // Thêm vào danh sách ShowtimeByRoomType
                showtimeByRoomTypeList.add(ShowtimeByRoomType.builder()
                        .roomTypeName(roomType.getName())
                        .roomTypeId(roomType.getId())
                        .showtimeByTimeResponseList(showtimeByTimeResponseList)
                        .build());
            }

            // Tạo đối tượng ShowtimeForUserResponse cho rạp hiện tại và thêm vào danh sách kết quả
            showtimeForUserResponses.add(ShowtimeForUserResponse.builder()
                    .theaterId(theater.getId())
                    .theaterName(theater.getName())
                    .showtimeByRoomTypeList(showtimeByRoomTypeList)
                    .build());
        }

        return showtimeForUserResponses;
    }





    @Transactional
    public List<ShowtimeByRoomResponse> getAllByAdmin(int theaterId, LocalDate date) {
        List<Room> listRoom = roomRepository.findByTheater(Theater.builder().id(theaterId).build());

        return listRoom.stream().map(room ->
                ShowtimeByRoomResponse
                        .builder()
                        .roomResponse(
                                RoomResponse.builder()
                                        .id(room.getId())
                                        .name(room.getName())
                                        .roomTypeName(room.getRoomType().getName())
                                        .roomTypeId(room.getRoomType().getId())
                                        .build())
                        .showtimeResponseList(
                                showTimeRepository.findByRoomAndDate(room, date)
                                        .stream()
                                        .map(showtime ->
                                                ShowtimeResponse
                                                        .builder()
                                                        .id(showtime.getId())
                                                        .movieTitle(showtime.getMovie().getTitle())
                                                        .timeStart(showtime.getTimeStart())
                                                        .timeEnd(showtime.getTimeEnd())
                                                        .isActive(showtime.getIsActive())
                                                        .build())
                                        .sorted(Comparator.comparing(ShowtimeResponse::getTimeStart)) // Sắp xếp theo thời gian bắt đầu
                                        .toList()

                        )
                        .build()
        ).toList();
    }
    @Transactional
    public int create(ShowtimeCreationDto showtimeCreationDto) {

        Movie movie = movieRepository.findById(showtimeCreationDto.getMovieId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        Room room = roomRepository.findById(showtimeCreationDto.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        var duration = movie.getDuration();
        var timeEnd = showtimeCreationDto.getTimeStart().plusMinutes(duration).plusMinutes(15);

        // Kiểm tra xung đột thời gian
        List<Showtime> conflictingShowtimes = showTimeRepository.findConflictingShowtimes(
                showtimeCreationDto.getRoomId(),
                showtimeCreationDto.getDate(),
                showtimeCreationDto.getTimeStart(),
                timeEnd
        );

        if (!conflictingShowtimes.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        }

        DayOfWeek dayOfWeek = showtimeCreationDto.getDate().getDayOfWeek();

        DayPrice dayPrice = dayPriceRepository.findByDayOfWeek(dayOfWeek)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        Showtime showtime = Showtime.builder()
                .date(showtimeCreationDto.getDate())
                .timeStart(showtimeCreationDto.getTimeStart())
                .timeEnd(timeEnd)
                .movie(movie)
                .room(room)
                .isActive(showtimeCreationDto.getIsActive()) // Set giá trị isActive
                .build();

        var savedShowtime = showTimeRepository.save(showtime);

        var extraPriceRoom = room.getRoomType().getExtraPrice();

        var listShowSeat = room.getRoomSeats().stream().map(roomSeat ->
                ShowSeat.builder()
                        .showtime(savedShowtime)
                        .seatTypeName(roomSeat.getSeatType().getName())
                        .seatRow(roomSeat.getSeatRow())
                        .number(roomSeat.getNumber())
                        .price(dayPrice.getBasePrice() + roomSeat.getSeatType().getExtraPrice() + extraPriceRoom)
                        .build()

        ).toList();
        showSeatRepository.saveAll(listShowSeat);

        return savedShowtime.getId();
    }
    @Transactional
    // Lấy một suất chiếu theo ID
    public ShowtimeResponse getById(int showtimeId) {
        Showtime showtime = showTimeRepository.findById(showtimeId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        return ShowtimeResponse.builder()
                .id(showtime.getId())
                .movieId(showtime.getMovie().getId())
                .roomId(showtime.getRoom().getId())
                .movieTitle(showtime.getMovie().getTitle())
                .timeStart(showtime.getTimeStart())
                .timeEnd(showtime.getTimeEnd())
                .isActive(showtime.getIsActive())
                .build();
    }
    @Transactional
    // Cập nhật thông tin suất chiếu
    public void updateShowtime(int showtimeId, ShowtimeUpdateDto showtimeUpdateDto) {
        Showtime showtime = showTimeRepository.findById(showtimeId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        Movie movie = movieRepository.findById(showtimeUpdateDto.getMovieId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        var duration = movie.getDuration();
        var timeEnd = showtimeUpdateDto.getTimeStart().plusMinutes(duration).plusMinutes(15);

        showtime.setMovie(movie);
        showtime.setTimeStart(showtimeUpdateDto.getTimeStart());
        showtime.setTimeEnd(timeEnd);
        showtime.setIsActive(showtimeUpdateDto.getIsActive()); // Cập nhật isActive

        showTimeRepository.save(showtime);
    }
    @Transactional
    // Xóa một suất chiếu theo ID
    public void deleteShowtime(int showtimeId) {
        Showtime showtime = showTimeRepository.findById(showtimeId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        showTimeRepository.delete(showtime);
    }



}
