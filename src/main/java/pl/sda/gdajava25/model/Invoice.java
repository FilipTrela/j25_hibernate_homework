package pl.sda.gdajava25.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Invoice implements IBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String nazwaKlienta;

    private boolean czyOpłacony;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDate dataWydania;

    @Formula(value = "(SELECT SUM((invoiceP.cena*1.22)*invoiceP.ilosc) FROM invoiceposition invoiceP WHERE invoiceP.invoice_id = id)")
    private Double kwota;

    private LocalDateTime dataIGodzinaOpłacenia;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "invoice", fetch = FetchType.EAGER)
    private Set<InvoicePosition> positionSet;

    public Invoice(String nazwaKlienta, boolean czyOpłacony) {
        this.nazwaKlienta = nazwaKlienta;
        this.czyOpłacony = czyOpłacony;
    }
}
