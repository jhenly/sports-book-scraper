package com.sportsbookscraper.app.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;


/**
 * Loads {@code config.properties} file and populates global configs and
 * individual Excel workbook, sheet specific configs.
 * <p>
 * Example {@code config.properties} file:
 *
 * <pre>
 * # path to Excel file
 * excel.file.path=/absolute/or/relative/path/to/excel/file
 *
 * # list of all sheets
 * all.sheets=NFL,NCAAF
 *
 * # properties shared by all sheets
 *
 * all.sheets.font=Calibri
 * all.sheets.font.size=11
 * all.sheets.cols.sizetofit=true
 * all.sheets.rows.sizetofit=false
 *
 * # individual sheet properties
 *
 * # NFL sheet
 * NFL.scrape.url=https\://classic.sportsbookreview.com/betting-odds/nfl-football/money-line/
 * NFL.sheet.title=NFL Football
 * NFL.sheet.title.row=0
 * NFL.sheet.title.col=0
 * NFL.sheet.table.row=1
 * NFL.sheet.table.teams.col=0
 * NFL.sheet.table.opener=true
 * NFL.sheet.table.opener.col=1
 * NFL.sheet.table.bookie.col=2
 *
 * # NCAAF sheet
 * NCAAF.scrape.url=https\://classic.sportsbookreview.com/betting-odds/college-football/money-line/
 * NCAAF.sheet.title=College Football
 * NCAAF.sheet.title.row=0
 * NCAAF.sheet.title.col=0
 * NCAAF.sheet.table.row=1
 * NCAAF.sheet.table.teams.col=0
 * NCAAF.sheet.table.opener=true
 * NCAAF.sheet.table.opener.col=1
 * NCAAF.sheet.table.bookie.col=2
 * </pre>
 *
 * @author Jonathan Henly
 */
final class WorkbookProperties extends AbstractProperties {
    // TODO remove CONFIG_CLASS_PATH <- it's just for debugging
    private final String CONFIG_CLASS_PATH = "./config/config.properties";

    /* - Begin static load config.properties file section - */
    private final Properties props;
    private final String excelFilePath;
    private final List<String> allSheets;
    // individual sheet properties holder
    private final List<SheetDataStore> sheetProps;
    
    
    // don't subclass this class
    WorkbookProperties(String propertiesFile)
    throws RequiredPropertyNotFoundException, IOException
    {
        this(propertiesFile, null);
    }
    
    // NOTE: do not change the order of calls in the following constructor, some
    // calls depend on other calls happening prior
    WorkbookProperties(String propertiesFile, String pathToExcelFile)
    throws IOException, RequiredPropertyNotFoundException
    {
        props = loadProps(propertiesFile);

        if (pathToExcelFile != null) {
            excelFilePath = getRequiredPropertyOrThrow(EXCEL_FILE_PATH);
        } else {
            excelFilePath = pathToExcelFile;
        }

        allSheets = initAllSheets();

        // init shared properties
        font = getStrPropOrDefault(SHEET_FONT, DEF_FONT);
        fontSize = getIntPropOrDefault(SHEET_FONT_SIZE, DEF_FONT_SIZE);
        colSizeToFit = getBoolPropOrDefault(COLS_SIZETOFIT, DEF_SIZE_TO_FIT);
        rowSizeToFit = getBoolPropOrDefault(ROWS_SIZETOFIT, DEF_SIZE_TO_FIT);

        // init all individual sheet properties
        sheetProps = loadSheetProperties(allSheets);
    }
    
    /* loads properties from a passed in file, or throws IOException */
    private Properties loadProps(String filename) throws IOException {
        Properties props = new Properties();
        
        try (InputStream input = WorkbookProperties.class.getClassLoader()
            .getResourceAsStream(filename))
        {
            if (input == null) {
                System.out.println("Sorry, unable to find " + filename);
            }
            
            props.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return props;
    }
    
    /* splits comma separated sheet names to an unmodifiable list, or throws */
    private List<String> initAllSheets()
        throws RequiredPropertyNotFoundException
    {
        // will throw if ALL_SHEETS property is not in config.properties
        String tmpAllSheets = getRequiredPropertyOrThrow(ALL_SHEETS);

        String[] sheetNames = tmpAllSheets.split(",");
        List<String> nonEmptySheetNames = new ArrayList<>(sheetNames.length);

        // iterate over sheetNames, remove trail/leading whitespace and add to
        // list if not an empty string
        for (int i = 0; i < sheetNames.length; i++) {
            String tmp = sheetNames[i].strip();
            if (!tmp.isEmpty()) {
                nonEmptySheetNames.add(tmp);
            }
        }

        // return unmodifiable list, it should not be changed
        return Collections.unmodifiableList(nonEmptySheetNames);
    }

    /* helper method used by methods retrieving required properties */
    private String getRequiredPropertyOrThrow(String property)
        throws RequiredPropertyNotFoundException
    {
        String propValue = props.getProperty(property);
        if (propValue == null) {
            throw new RequiredPropertyNotFoundException(property,
                CONFIG_CLASS_PATH);
        }
        return propValue;
    }

    /* helper int property getter */
    private int getIntPropOrDefault(String prop, int def) {
        String tmp = props.getProperty(prop);
        if (tmp != null) {
            int val;
            try {
                val = Integer.parseInt(tmp);
            } catch (NumberFormatException nfe) {
                return def;
            }

            return (val >= 0) ? val : def;
        }

        return def;
    }

    /* helper string property getter */
    private String getStrPropOrDefault(String prop, String def) {
        String tmp = props.getProperty(prop);
        return (tmp != null) ? tmp : def;
    }

    /* helper boolean property getter */
    private boolean getBoolPropOrDefault(String prop, boolean def) {
        String tmp = props.getProperty(prop);
        return (tmp != null) ? Boolean.parseBoolean(tmp) : def;
    }

    /* accessors section */
    
    /**
     * @return the path to the Excel file, if set in {@code config.properties}
     */
    @Override
    public String getExcelFilePath() {
        return excelFilePath;
    }

    /**
     * @return returns an {@linkplain Collections#unmodifiableList(List)
     *         unmodifiable list} containing the Excel workbook's sheet names.
     */
    @Override
    public List<String> getSheetNames() {
        return allSheets;
    }

    /**
     * @return font name used by all sheets
     */
    @Override
    public String getSheetFont() {
        return font;
    }

    /**
     * @return font size used by all sheets
     */
    @Override
    public int getSheetFontSize() {
        return fontSize;
    }

    /**
     * @return {@code true} if Excel column widths should fit their content,
     *         {@code false}
     */
    @Override
    public boolean getColSizeToFit() {
        return colSizeToFit;
    }

    /**
     * @return {@code true} if Excel row heights should fit their content,
     *         otherwise {@code false}
     */
    @Override
    public boolean getRowSizeToFit() {
        return rowSizeToFit;
    }

    /**
     * @param index - which sheet's properties to retrieve
     * @return the sheet properties associated with sheet index supplied
     */
    @Override
    public SheetDataStore getSheetProperties(int index) {
        return sheetProps.get(index);
    }

    /* -- SheetProperties Section -- */

    /* iterates over sheet names and creates sheet properties */
    private List<SheetDataStore> loadSheetProperties(List<String> sheets) {
        List<SheetDataStore> sprops = new ArrayList<>(sheets.size());

        // build sheet properties for each sheet
        for (String sheetName : sheets) {
            sprops.add(buildSheetProperties(sheetName));
        }

        return Collections.unmodifiableList(sprops);
    }
    
    /* builds sheet properties using the supplied sheet name */
    private SheetDataStore buildSheetProperties(String sheet) {
        SheetProperties sp = new SheetProperties();

        // if no url then default, will be handled later in mediator
        sp.scrapeUrl = getStrPropOrDefault(sheet + SCRAPE_URL, "");

        // if no sheet title specified, then set title to sheet name
        sp.sheetTitle = getStrPropOrDefault(sheet + SHEET_TITLE, sheet);
        // if no title row specified then use default
        sp.titleRow = getIntPropOrDefault(sheet + TITLE_ROW, DEF_TITLE_ROW_COL);
        // if no title col specified then use default
        sp.titleCol = getIntPropOrDefault(sheet + TITLE_COL, DEF_TITLE_ROW_COL);
        // if no opener specified then use default
        sp.opener = getBoolPropOrDefault(sheet + OPENER, DEF_HAS_OPENER);
        // if not teams col specified then use default
        sp.teamsCol = getIntPropOrDefault(sheet + TEAMS_COL, DEF_TEAMS_COL);

        // now we have to range check any row or column index, for instance
        // tableRow should be greater than titleRow
        int tmp;

        // get tableRow prop, set it to titleRow + 1 if it's less than titleRow
        tmp = getIntPropOrDefault(sheet + SHEET_TABLE, DEF_TABLE_ROW);
        sp.tableRow = (tmp > sp.titleRow) ? tmp : sp.titleRow + 1;

        // get tableRow prop, set it to titleRow + 1 if it's less than titleRow
        tmp = getIntPropOrDefault(sheet + SHEET_TABLE, DEF_TABLE_ROW);
        sp.tableRow = (tmp > sp.titleRow) ? tmp : sp.titleRow + 1;

        // get openerCol prop, set it to teamsCol + 1 if it's less than teamsCol
        tmp = getIntPropOrDefault(sheet + OPENER_COL, DEF_OPENER_COL);
        sp.openerCol = (tmp > sp.teamsCol) ? tmp : sp.teamsCol + 1;

        // get openerCol prop, set it to teamsCol + 1 if it's less than teamsCol
        tmp = getIntPropOrDefault(sheet + BOOKIE_COL, DEF_BOOKIE_COL);
        // account for a sheet not having an opener column
        if (sp.hasOpener()) {
            sp.bookieCol = (tmp > sp.openerCol) ? tmp : sp.openerCol + 1;
        } else {
            sp.bookieCol = (tmp > sp.teamsCol) ? tmp : sp.teamsCol + 1;
        }

        return sp;
    }
    
} // public final class WorkbookProperties