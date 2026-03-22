public class Main {

    public static void main(String[] args) {

        System.out.println("===== DECORATOR (REPORTS) =====");

        IReport report = new SalesReport();

        // накладываем декораторы
        report = new DateFilterDecorator(report, "2025-01-01", "2025-12-31");
        report = new SortingDecorator(report, "amount");
        report = new CsvExportDecorator(report);

        System.out.println(report.generate());


        System.out.println("\n===== ADAPTER (DELIVERY) =====");

        IInternalDeliveryService service1 =
                DeliveryServiceFactory.getService("internal");

        service1.deliverOrder("ORD-1");
        System.out.println(service1.getDeliveryStatus("ORD-1"));

        IInternalDeliveryService service2 =
                DeliveryServiceFactory.getService("A");

        service2.deliverOrder("ORD-2");
        System.out.println(service2.getDeliveryStatus("ORD-2"));

        IInternalDeliveryService service3 =
                DeliveryServiceFactory.getService("B");

        service3.deliverOrder("ORD-3");
        System.out.println(service3.getDeliveryStatus("ORD-3"));
    }

    // =========================
    // DECORATOR
    // =========================

    interface IReport {
        String generate();
    }

    static class SalesReport implements IReport {
        public String generate() {
            return "Sales Report: [Order1: 100$, Order2: 200$]";
        }
    }

    static class UserReport implements IReport {
        public String generate() {
            return "User Report: [User1, User2, User3]";
        }
    }

    static abstract class ReportDecorator implements IReport {
        protected IReport report;

        public ReportDecorator(IReport report) {
            this.report = report;
        }
    }

    static class DateFilterDecorator extends ReportDecorator {
        private String from;
        private String to;

        public DateFilterDecorator(IReport report, String from, String to) {
            super(report);
            this.from = from;
            this.to = to;
        }

        public String generate() {
            return report.generate() +
                    "\n[Filtered by date: " + from + " - " + to + "]";
        }
    }

    static class SortingDecorator extends ReportDecorator {
        private String field;

        public SortingDecorator(IReport report, String field) {
            super(report);
            this.field = field;
        }

        public String generate() {
            return report.generate() +
                    "\n[Sorted by: " + field + "]";
        }
    }

    static class CsvExportDecorator extends ReportDecorator {
        public CsvExportDecorator(IReport report) {
            super(report);
        }

        public String generate() {
            return report.generate() + "\n[Exported to CSV]";
        }
    }

    static class PdfExportDecorator extends ReportDecorator {
        public PdfExportDecorator(IReport report) {
            super(report);
        }

        public String generate() {
            return report.generate() + "\n[Exported to PDF]";
        }
    }

    // дополнительный декоратор (из задания)
    static class AmountFilterDecorator extends ReportDecorator {
        private int min;

        public AmountFilterDecorator(IReport report, int min) {
            super(report);
            this.min = min;
        }

        public String generate() {
            return report.generate() +
                    "\n[Filtered by amount > " + min + "]";
        }
    }

    // =========================
    // ADAPTER
    // =========================

    interface IInternalDeliveryService {
        void deliverOrder(String orderId);
        String getDeliveryStatus(String orderId);
    }

    static class InternalDeliveryService implements IInternalDeliveryService {

        public void deliverOrder(String orderId) {
            System.out.println("Internal delivery: " + orderId);
        }

        public String getDeliveryStatus(String orderId) {
            return "Internal status: Delivered " + orderId;
        }
    }

    // ===== External A =====
    static class ExternalLogisticsServiceA {
        public void shipItem(int itemId) {
            System.out.println("Service A shipping item: " + itemId);
        }

        public String trackShipment(int shipmentId) {
            return "Service A tracking: " + shipmentId;
        }
    }

    static class LogisticsAdapterA implements IInternalDeliveryService {

        private ExternalLogisticsServiceA service = new ExternalLogisticsServiceA();

        public void deliverOrder(String orderId) {
            System.out.println("[Adapter A]");
            service.shipItem(orderId.hashCode());
        }

        public String getDeliveryStatus(String orderId) {
            return service.trackShipment(orderId.hashCode());
        }
    }

    // ===== External B =====
    static class ExternalLogisticsServiceB {
        public void sendPackage(String packageInfo) {
            System.out.println("Service B sending: " + packageInfo);
        }

        public String checkPackageStatus(String trackingCode) {
            return "Service B status: " + trackingCode;
        }
    }

    static class LogisticsAdapterB implements IInternalDeliveryService {

        private ExternalLogisticsServiceB service = new ExternalLogisticsServiceB();

        public void deliverOrder(String orderId) {
            System.out.println("[Adapter B]");
            service.sendPackage(orderId);
        }

        public String getDeliveryStatus(String orderId) {
            return service.checkPackageStatus(orderId);
        }
    }

    // ===== NEW SERVICE (доп. задание) =====
    static class ExternalLogisticsServiceC {
        public void process(String id) {
            System.out.println("Service C processing: " + id);
        }

        public String status(String id) {
            return "Service C status: " + id;
        }
    }

    static class LogisticsAdapterC implements IInternalDeliveryService {

        private ExternalLogisticsServiceC service = new ExternalLogisticsServiceC();

        public void deliverOrder(String orderId) {
            service.process(orderId);
        }

        public String getDeliveryStatus(String orderId) {
            return service.status(orderId);
        }
    }

    // =========================
    // FACTORY
    // =========================

    static class DeliveryServiceFactory {

        public static IInternalDeliveryService getService(String type) {

            switch (type) {
                case "internal":
                    return new InternalDeliveryService();
                case "A":
                    return new LogisticsAdapterA();
                case "B":
                    return new LogisticsAdapterB();
                case "C":
                    return new LogisticsAdapterC();
                default:
                    throw new RuntimeException("Unknown service");
            }
        }
    }
}