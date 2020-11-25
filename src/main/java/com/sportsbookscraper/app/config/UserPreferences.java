package com.sportsbookscraper.app.config;

/**
 * This class is used to load user settings from a backing data store, which is
 * operating system dependent.
 * <p>
 * This class contains methods to retrieve application specific settings and
 * individual settings for sheets in an Excel workbook.
 * <p>
 * <b>Note:</b> if no user settings can be found via Java's preferences API,
 * then the {@linkplain UserProperties} class will be used to load settings from
 * {@code config.properties}. After which, the application will try to use the
 * Java preferences API to save the loaded settings to an OS dependent backing
 * data store.
 * 
 * @author Jonathan Henly
 */
class UserPreferences {
    
    UserPreferences() {
        
    }
    
}
