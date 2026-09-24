package com.carsharing;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

public class TestRunner {
    public static void main(String[] args) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage("com.carsharing"))
                .build();

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);

        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();
        System.out.println("==================================================");
        System.out.println("              TEST EXECUTION SUMMARY              ");
        System.out.println("==================================================");
        System.out.println("Tests found:     " + summary.getTestsFoundCount());
        System.out.println("Tests started:   " + summary.getTestsStartedCount());
        System.out.println("Tests succeeded: " + summary.getTestsSucceededCount());
        System.out.println("Tests failed:    " + summary.getTestsFailedCount());
        System.out.println("Tests aborted:   " + summary.getTestsAbortedCount());
        System.out.println("==================================================");

        if (!summary.getFailures().isEmpty()) {
            System.out.println("FAILURES:");
            summary.getFailures().forEach(f -> {
                System.out.println(f.getTestIdentifier().getDisplayName() + ": " + f.getException().getMessage());
                f.getException().printStackTrace();
            });
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED SUCCESSFULLY!");
            System.exit(0);
        }
    }
}
