package pl.sda.gdajava25;

import pl.sda.gdajava25.hibernate.EntityDAO;
import pl.sda.gdajava25.model.Invoice;
import pl.sda.gdajava25.model.InvoicePosition;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Witam w bazie danych do zarządzania rachunkami w restauracji.");
        EntityDAO dao = new EntityDAO();

        String komenda;
        do {
            komenda = SCANNER.nextLine();
            if (komenda.equalsIgnoreCase("dodaj rachunek")) {
                addInvoice(dao);
            } else if (komenda.equalsIgnoreCase("dodaj produkt")) {
                addInvoicePosition(dao);
            } else if (komenda.equalsIgnoreCase("rachunek oplacony")) {
                setInvoiceAsPaid(dao);
            } else if (komenda.equalsIgnoreCase("sprawdz kwote")) {
                checkKwotaByInvoiceID(dao);
            } else if (komenda.equalsIgnoreCase("list produkty z rachunku")) {
                listAllInvoicePostionById(dao);
            } else if (komenda.equalsIgnoreCase("list rachunki")) {
                listAllInvoice(dao);
            } else if (komenda.equalsIgnoreCase("list produkty")) {
                listAllInvoicePostion(dao);
            } else if (komenda.equalsIgnoreCase("list rachunki nieoplacone")) {
                listInvoiceNotPaid(dao);
            } else if (komenda.equalsIgnoreCase("list rachunki ostatni")) {
                listInvoiceLastWeek(dao);
            } else if (komenda.equalsIgnoreCase("suma kwot rachunki")) {
                printSumOfTodayInvoice(dao);
            }

        } while (!komenda.equalsIgnoreCase("quit"));


    }

    private static void printSumOfTodayInvoice(EntityDAO dao) {
        double suma = dao.getAll(Invoice.class).stream()
                .filter(invoice -> invoice.getDataWydania().isEqual(LocalDate.now()))
                .mapToDouble(Invoice::getKwota).sum();
        System.out.println("Suma dzisiejszch rachunków to : " + suma + " zł.");
    }

    private static void listInvoiceLastWeek(EntityDAO dao) {
        dao.getAll(Invoice.class).stream()
                .filter(invoice -> invoice.getDataWydania().isAfter(LocalDate.now().minusDays(7)))
                .forEach(System.out::println);
    }

    private static void listInvoiceNotPaid(EntityDAO dao) {
        dao.getAll(Invoice.class).stream()
                .filter(Invoice::isCzyOpłacony)
                .forEach(System.out::println);
    }

    private static void listAllInvoicePostion(EntityDAO dao) {
        dao.getAll(InvoicePosition.class).forEach(System.out::println);
    }

    private static void listAllInvoice(EntityDAO dao) {
        dao.getAll(Invoice.class).forEach(System.out::println);
    }

    private static void listAllInvoicePostionById(EntityDAO dao) {
        Invoice invoice = getInvoice(dao);
        invoice.getPositionSet().forEach(System.out::println);
    }

    private static void checkKwotaByInvoiceID(EntityDAO dao) {
        Invoice invoice = getInvoice(dao);
        System.out.println("Kwota na rachunku o id " + invoice.getId() + ". Wynosi : " + invoice.getKwota() + " zł.");
    }

    private static void setInvoiceAsPaid(EntityDAO dao) {
        Invoice invoice = getInvoice(dao);
        invoice.setCzyOpłacony(true);
        invoice.setDataIGodzinaOpłacenia(LocalDateTime.now());
        dao.saveOrUpdate(invoice);

    }

    private static void addInvoicePosition(EntityDAO dao) {
        Invoice invoice = getInvoice(dao);
        if (invoice.isCzyOpłacony()) {
            System.out.println("Rachunek opłacony i zamknięty !");
        } else
            dao.saveOrUpdate(makeInvoicePosition(invoice));
    }

    private static Invoice getInvoice(EntityDAO dao) {
        Optional<Invoice> invoice = getInvoiceById(dao);
        return invoice.orElse(null);
    }

    private static InvoicePosition makeInvoicePosition(Invoice invoice) {
        InvoicePosition invoicePosition = new InvoicePosition();
        invoicePosition.setInvoice(invoice);
        System.out.println("Podaj nazwe produktu : ");
        invoicePosition.setNazwa(SCANNER.nextLine());
        System.out.println("Podaj cene : ");
        invoicePosition.setCena(Double.parseDouble(SCANNER.nextLine()));
        System.out.println("Podaj ilość : ");
        invoicePosition.setIlosc(Integer.parseInt(SCANNER.nextLine()));
        return invoicePosition;
    }

    private static Optional<Invoice> getInvoiceById(EntityDAO dao) {
        System.out.println("Podaj id rachuku : ");
        Long l = Long.parseLong(SCANNER.nextLine());
        return dao.getById(Invoice.class, l);
    }

    private static void addInvoice(EntityDAO dao) {
        Invoice invoice = makeInvoice();
        dao.saveOrUpdate(invoice);
        String komenda;
        System.out.println("Chcesz dodać produkty [Y/N] ? : ");
        do {
            komenda = SCANNER.nextLine();
            if (komenda.equalsIgnoreCase("N")) {
                break;
            } else if (komenda.equalsIgnoreCase("Y")) {
                addInvoicePositionToInvoice(invoice, dao);
            }
        } while (isaYorN(komenda));

    }

    private static void addInvoicePositionToInvoice(Invoice invoice, EntityDAO dao) {
        Boolean czyKolejny;
        do {
            dao.saveOrUpdate(makeInvoicePosition(invoice));
            System.out.println("Czy chcesz dodać kolejny [Y/N] ? : ");
            czyKolejny = trueOrFalse();
        } while (czyKolejny);
    }

    private static Boolean trueOrFalse() {
        String s = SCANNER.nextLine();
        do {
            if (s.equalsIgnoreCase("N")) {
                return false;
            } else if (s.equalsIgnoreCase("Y")) {
                return true;
            }
        } while (isaYorN(s));
        return false;
    }

    private static boolean isaYorN(String komenda) {
        return !komenda.equalsIgnoreCase("Y") && !komenda.equalsIgnoreCase("N");
    }

    private static Invoice makeInvoice() {
        Invoice invoice = new Invoice();
        System.out.println("Podaj nazwe klienta : ");
        invoice.setNazwaKlienta(SCANNER.nextLine());
        System.out.println("Czy opłacony [Y/N] ? : ");
        invoice.setCzyOpłacony(trueOrFalse());
        return invoice;
    }
}
