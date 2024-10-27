package com.hiepnt.moviebooking.service;

import com.hiepnt.moviebooking.dto.request.RoomCreationDto;
import com.hiepnt.moviebooking.dto.request.RoomUpdateDto;
import com.hiepnt.moviebooking.dto.response.RoomResponse;
import com.hiepnt.moviebooking.entity.*;
import com.hiepnt.moviebooking.exception.AppException;
import com.hiepnt.moviebooking.exception.ErrorCode;
import com.hiepnt.moviebooking.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {
    RoomRepository roomRepository;
    RoomTypeRepository roomTypeRepository;
    TheaterRepository theaterRepository;
    SeatTypeRepository seatTypeRepository;
    RoomSeatRepository roomSeatRepository;
    public RoomResponse create(RoomCreationDto roomCreationDto) {
        if(roomCreationDto==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        RoomType roomType = roomTypeRepository.findById(roomCreationDto.getRoomTypeId())
                .orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        Theater theater = theaterRepository.findById(roomCreationDto.getTheaterId())
                .orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        Room room = Room.builder()
                .name(roomCreationDto.getName())
                .roomType(roomType)
                .theater(theater)
                .build();
        var savedRoom = roomRepository.save(room);

        SeatType standardSeatType = seatTypeRepository.findByName("STANDARD")
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        SeatType vipSeatType = seatTypeRepository.findByName("VIP")
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        SeatType coupleSeatType = seatTypeRepository.findByName("COUPLE")
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        // Tạo danh sách RoomSeat
        List<RoomSeat> roomSeats = new ArrayList<>();
        int totalRows = roomCreationDto.getRow();
        int totalCols = roomCreationDto.getCol();

        for (int i = 1; i <= totalRows; i++) { // row là số hàng
            for (int j = 1; j <= totalCols; j++) { // col là số cột
                SeatType seatType;

                // Gán loại ghế cho từng hàng
                if (i <= 3) {
                    seatType = standardSeatType; // 3 hàng đầu là STANDARD
                } else if (i == totalRows) {
                    seatType = coupleSeatType; // Hàng cuối là COUPLE
                } else {
                    seatType = vipSeatType; // Các hàng còn lại là VIP
                }

                RoomSeat roomSeat = RoomSeat.builder()
                        .seatRow(Character.toString((char) ('A' + (i - 1)))) // Tạo row kiểu 'A', 'B', 'C',...
                        .number(j) // Số ghế theo cột
                        .room(savedRoom)
                        .seatType(seatType) // Gán SeatType tương ứng
                        .build();
                roomSeats.add(roomSeat);
            }
        }

        // Lưu danh sách RoomSeat vào database
        roomSeatRepository.saveAll(roomSeats);


        return RoomResponse.builder()
                .id(savedRoom.getId())
                .name(savedRoom.getName())
                .roomTypeName(roomType.getName())
                .build();




    }

    public List<RoomResponse> getAll(int theaterId) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        List<Room> roomList = roomRepository.findByTheater(theater);

        return roomList.stream()
                .map(room->
                        RoomResponse.builder()
                                .id(room.getId())
                                .name(room.getName())
                                .roomTypeName(room.getRoomType().getName())
                                .build())
                .toList();
    }

    public String delete(int id) {
        roomRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        roomRepository.deleteById(id);
        return "Delete success";
    }

    public RoomResponse get(int id) {
        Room room = roomRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.NOT_EXISTED));
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .roomTypeName(room.getRoomType().getName())
                .roomTypeId(room.getRoomType().getId())
                .build();
    }

    public RoomResponse update(int id, RoomUpdateDto roomUpdateDto){
        if(roomUpdateDto==null) throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        room.setName(roomUpdateDto.getName());
        room.setRoomType(RoomType.builder().id(roomUpdateDto.getRoomTypeId()).build());
        var savedRoom = roomRepository.save(room);
        return RoomResponse.builder()
                .id(savedRoom.getId())
                .name(savedRoom.getName())
                .roomTypeName(savedRoom.getRoomType().getName())
                .roomTypeId(savedRoom.getRoomType().getId())
                .build();
    }
}
