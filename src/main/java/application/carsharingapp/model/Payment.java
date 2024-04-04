package application.carsharingapp.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;

@Getter
@Setter
@SoftDelete
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "varchar", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(columnDefinition = "varchar", nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rental_id")
    private Rental rental;
    @Column(columnDefinition = "varchar", name = "session_url", nullable = false)
    private String sessionUrl;
    @Column(name = "session_id", nullable = false)
    private String sessionId;
    @Column(nullable = false)
    private BigDecimal amount;

    public enum Status {
        PENDING,
        PAID
    }

    public enum Type {
        PAYMENT,
        FINE
    }
}
