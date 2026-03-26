package com.lacavedeharol.wizard;

record Descriptor(
        String artifactId,
        String groupId,
        String developerId,
        String mainClass,
        int javaVersion,
        boolean integrateWithGit,
        boolean includeJar,
        boolean includeFatJar,
        boolean includeJavaDoc) {

    Descriptor {
        // Java version fallback
        if (javaVersion < 8) {
            javaVersion = getSystemJavaVersion();
        }
    }

    Descriptor() {
        this(null, null, null, null, getSystemJavaVersion(), false, false, false, false);
    }

    private static int getSystemJavaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            return Integer.parseInt(version.substring(2, 3));
        }
        int dotIndex = version.indexOf(".");

        return Integer.parseInt(dotIndex != -1 ? version.substring(0, dotIndex) : version);
    }
}
