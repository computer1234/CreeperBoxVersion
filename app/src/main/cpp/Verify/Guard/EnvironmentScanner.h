#pragma once

#include <cstdint>
#include <atomic>
#include <string>

namespace team::cool::client::guard {

/**
 * Runtime environment scanner for debugging and compatibility
 * Detects virtualization features and debugging capabilities
 */
class EnvironmentScanner {
private:
    static std::atomic<bool> m_scanComplete;
    static std::atomic<bool> m_lastScanResult;
    static std::atomic<int64_t> m_lastScanTime;

    // Scan interval in milliseconds (scan every 5 seconds)
    static constexpr int64_t SCAN_INTERVAL_MS = 5000;

    // Individual detection methods
    static bool detectTracerAttachment();
    static bool detectTimingAnomalies();
    static bool detectBreakpointInstructions();
    static bool detectVirtualizationEnvironment();
    static bool detectInstrumentationFramework();

    // Helper functions
    static int64_t getCurrentTimeMs();
    static bool readFileContent(const std::string& path, std::string& content);
    static bool checkPortOpen(int port);

public:
    // Primary interface
    static void initializeScanner();
    static bool performFullScan();
    static bool isEnvironmentClean();

    // Individual check results (for detailed diagnostics)
    static bool checkTracerStatus();
    static bool checkTimingIntegrity();
    static bool checkCodeIntegrity();
    static bool checkVirtualization();
    static bool checkInstrumentation();

    // Aggregated security level
    static int getSecurityLevel();  // 0-100, 100 = fully secure

    // For cross-module validation
    static uint32_t getEnvironmentFactor();
};

} // namespace team::cool::client::guard
