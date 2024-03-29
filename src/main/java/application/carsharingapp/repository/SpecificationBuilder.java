package application.carsharingapp.repository;

import application.carsharingapp.dto.rental.RentalSearchParameters;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(RentalSearchParameters searchParameters);
}
