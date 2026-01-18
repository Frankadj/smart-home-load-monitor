package recommendation;

import alerts.AlertManager;
import models.Appliance;
import models.SocketGroup;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RecommendationEngine {

    private AlertManager alertManager;

    public RecommendationEngine(AlertManager alertManager) {
        this.alertManager = alertManager;
    }

    /**
     * Generates load reduction recommendations when a socket group is overloaded (>13A).
     * Prioritizes turning off high-current, non-essential appliances first.
     */
    public void generateLoadReductionRecommendations(SocketGroup group) {
        double totalCurrent = group.getTotalCurrent();

        if (totalCurrent <= 13.0) {
            return; // No overload, no recommendations needed
        }

        alertManager.alert(group.getName() + " socket group overloaded (" +
                String.format("%.1f", totalCurrent) + "A). Reduce load immediately.");

        // Get all currently on appliances (assuming isOn field exists or use current > 0)
        List<Appliance> onAppliances = group.getAppliances().stream()
                .filter(a -> a.isOn() || a.getCurrent() > 0) // Use isOn if added, else fallback
                .sorted(Comparator.comparingDouble(Appliance::getCurrent).reversed())
                .collect(Collectors.toList());

        if (onAppliances.isEmpty()) {
            alertManager.alert("No active appliances to recommend turning off.");
            return;
        }

        // Recommend top 1–2 highest load appliances
        int recommendations = Math.min(2, onAppliances.size());

        alertManager.alert("Recommended actions:");
        for (int i = 0; i < recommendations; i++) {
            Appliance a = onAppliances.get(i);
            String suggestion = "→ Turn off " + a.getName() +
                    " (" + String.format("%.1f", a.getCurrent()) + "A)";
            if (a.getPriority() != null && a.getPriority() == Appliance.Priority.NON_ESSENTIAL) {
                suggestion += " (non-essential)";
            }
            alertManager.alert(suggestion);
        }

        // If still overloaded after recommendations, suggest checking house level
        if (totalCurrent - onAppliances.get(0).getCurrent() > 13.0) {
            alertManager.alert("Consider reducing load in other groups as well.");
        }
    }

    /**
     * Optional: House-level recommendation (if total exceeds main limit)
     */
    public void generateHouseLevelRecommendations(House house) {
        if (house.getTotalCurrent() > house.getMainLimit()) {
            alertManager.alert("Whole house limit exceeded. Prioritize turning off non-essential appliances across all groups.");
        }
    }
}
