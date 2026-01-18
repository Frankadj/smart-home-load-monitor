public class Appliance {
    private String name;
    private String group;
    private double nominalCurrent; // Amps when on
    private double maxAllowed;
    private boolean isOn;
    private double currentReading;
    private String status = "OK";
    private double previousReading = 0;

    public Appliance(String name, String group, double nominalCurrent, double maxAllowed) {
        this.name = name;
        this.group = group;
        this.nominalCurrent = nominalCurrent;
        this.maxAllowed = maxAllowed;
        this.isOn = false;
        this.currentReading = 0;
    }

    public String getName() { return name; }
    public String getGroup() { return group; }
    public double getNominalCurrent() { return nominalCurrent; }
    public boolean isOn() { return isOn; }
    public void setOn(boolean on) { isOn = on; }
    public double getCurrentReading() { return currentReading; }
    public void setCurrentReading(double reading) { 
        previousReading = currentReading;
        currentReading = reading; 
    }
    public double getPreviousReading() { return previousReading; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getMaxAllowed() { return maxAllowed; }
}
