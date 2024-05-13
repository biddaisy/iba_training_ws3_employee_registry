package eu.ibagroup.bot.config;

public final class Version {

    public static final String BUILD_NUMBER = "${revision}";

    public static final String BUILD_TIME = "${timestamp}";

    public static final String POM_VERSION =  "${project.version}";

}