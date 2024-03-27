package application.carsharingapp.validation;

import application.carsharingapp.dto.user.UserRegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;

public class FieldMatchValidator implements
        ConstraintValidator<FieldMatch, UserRegistrationRequestDto> {
    @Override
    public boolean isValid(UserRegistrationRequestDto request,
                           ConstraintValidatorContext constraintValidatorContext) {
        return Objects.equals(request.getPassword(), request.getRepeatPassword());
    }
}
