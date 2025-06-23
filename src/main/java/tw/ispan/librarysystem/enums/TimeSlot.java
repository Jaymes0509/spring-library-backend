package tw.ispan.librarysystem.enums;

public enum TimeSlot {
    SLOT_09_11("09:00", "11:00"),
    SLOT_11_13("11:00", "13:00"),
    SLOT_13_15("13:00", "15:00"),
    SLOT_15_17("15:00", "17:00"),
    SLOT_17_19("17:00", "19:00"),
    SLOT_19_21("19:00", "21:00");

    private final String start;
    private final String end;

    TimeSlot(String start, String end) {
        this.start = start;
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public static TimeSlot fromLabel(String label) {
        for (TimeSlot slot : values()) {
            if ((slot.start + " - " + slot.end).equals(label)) {
                return slot;
            }
        }
        throw new IllegalArgumentException("無效的時段: " + label);
    }

    @Override
    public String toString() {
        return start + " - " + end;
    }
}


