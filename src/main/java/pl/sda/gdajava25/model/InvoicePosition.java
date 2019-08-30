package pl.sda.gdajava25.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Formula;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class InvoicePosition implements IBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String nazwa;

    @Column(nullable = false)
    private double cena;

    @Formula(value = "(SELECT ip.cena*0.22 FROM invoiceposition ip)")
    private double kwotaPodatku;

    @Column(nullable = false)
    private int ilosc;

    @ToString.Exclude
    @ManyToOne
    private Invoice invoice;

    public InvoicePosition(String nazwa, double cena, int ilosc, Invoice invoice) {
        this.nazwa = nazwa;
        this.cena = cena;
        this.ilosc = ilosc;
        this.invoice = invoice;
    }
}
