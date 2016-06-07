package com.example.hisashi.listviewlearning;

/**
 * Created by hisashi on 2015-10-28.
 */
public class Record {
    private String clientName;

    private String displayNumber;
    private String description;
    private String openDate;
    private String status;
    public Record(final String clientName, final String displayNumber, final String description,
        final String openDate, final String status) {
        this.clientName = clientName;
        this.displayNumber = displayNumber;
        this.description = description;
        this.openDate = openDate;
        this.status = status;
    }

    public String getClientName() {
        return clientName;
    }

    public String getDisplayNumber() {
        return displayNumber;
    }

    public String getDescription() {
        return description;
    }

    public String getOpenDate() {
        return openDate;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object object) {
        Record that = (Record) object;
        boolean clientNameSame = (this.clientName == null || this.clientName.isEmpty()) ? true : this.clientName.equals(that.clientName);
        boolean displayNumberSame = (this.displayNumber == null || this.displayNumber.isEmpty()) ? true : this.displayNumber.equals(that.displayNumber);
        boolean descriptionSame = (this.description == null || this.description.isEmpty()) ? true : this.description.equals(that.description);
        boolean openDateSame = (this.openDate == null || this.openDate.isEmpty()) ? true : this.openDate.equals(that.openDate);
        boolean statusSame = (this.status == null || this.status.isEmpty()) ? true : this.status.equals(that.status);

        if (clientNameSame && displayNumberSame && descriptionSame && openDateSame && statusSame) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return this.clientName + ", " + this.getDescription() + ", " +
                this.getOpenDate() + ", " + this.getOpenDate();
    }
}
