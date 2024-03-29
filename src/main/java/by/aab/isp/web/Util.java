package by.aab.isp.web;

public class Util {
    private Util() {}

    private static class BillPughSingleton {
        private static final Util INSTANCE = new Util();
    }

    public static Util getInstance() {
        return BillPughSingleton.INSTANCE;
    }

    private static final String[] BANDWIDTH_RANGES = {" Kb/s", " Mb/s", " Gb/s", " Tb/s"};

    public String formatBandwidth(Integer kbps) {
        if (null == kbps) {
            return "unlimited";
        }
        if (kbps < 0) {
            throw new IllegalArgumentException("Bandwidth must be >= 0 or null");
        }
        double bandwidth = kbps;
        for (int i = 0; i < BANDWIDTH_RANGES.length - 1; i++) {
            if (bandwidth < 1024) {
                return bandwidth + BANDWIDTH_RANGES[i];
            }
            bandwidth = bandwidth / 1024;
        }
        return bandwidth + BANDWIDTH_RANGES[BANDWIDTH_RANGES.length - 1];
    }

    private static final String[] TRAFFIC_RANGES = {" B", " KB", " MB", " GB", " TB", " PB"};

    public String formatTraffic(Long bytes) {
        if (null == bytes) {
            return "unlimited";
        }
        if (bytes < 0) {
            throw new IllegalArgumentException("Traffic must be >= 0 or null");
        }
        if (bytes < 1024) {
            return bytes + TRAFFIC_RANGES[0];
        }
        double traffic = (double) bytes / 1024;
        for (int i = 1; i < TRAFFIC_RANGES.length - 1; i++) {
            if (traffic < 1024) {
                return traffic + TRAFFIC_RANGES[i];
            }
            traffic = traffic / 1024;
        }
        return traffic + TRAFFIC_RANGES[TRAFFIC_RANGES.length - 1];
    }

}
