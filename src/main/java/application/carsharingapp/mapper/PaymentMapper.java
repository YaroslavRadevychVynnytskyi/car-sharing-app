package application.carsharingapp.mapper;

import application.carsharingapp.config.MapperConfig;
import application.carsharingapp.dto.payment.PaymentResponseDto;
import application.carsharingapp.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(target = "rentalId", source = "payment.rental.id")
    PaymentResponseDto toDto(Payment payment);
}
