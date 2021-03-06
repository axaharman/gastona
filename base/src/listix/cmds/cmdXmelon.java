/*
library listix (www.listix.org)
Copyright (C) 2005 Alejandro Xalabarder Aulet

This program is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation; either version 3 of the License, or (at your option) any later
version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
this program; if not, write to the Free Software Foundation, Inc., 59 Temple
Place - Suite 330, Boston, MA 02111-1307, USA.
*/

/*
   //(o) WelcomeGastona_source_listix_command XMELON

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       XMELON
   <groupInfo>  data_parsers
   <javaClass>  listix.cmds.cmdXmelon
   <importance> 4
   <desc>       //Parses a XML file and stores the data into a database

   <help>
      //
      // The XMELON schema is an approach to parse xml data and store it in a database. This command
      // allows to parse xml files creating and filling the XMELON tables.
      //
      // ====== Parsing XML files into XMELON tables
      //
      // To parse a XML file simply give the file name of the xml file and the target database, for example
      //
      //       XMELON, FILE2DB, file2parse.xml, xmelondb.db
      //
      // if the database does not exist it will be created.
      //
      // Multiple files can be parsed into the same database even having totally different xml schemas.
      // Nevertheless, if multiple file parsing is planned it is convenient to switch on a special XMELON cache
      // that accelerates this kind of parsing since keeps in "xmelon mind" the tables of tags and paths for a
      // faster search of these elements needed on each record insertion. For example:
      //
      //       XMELON, CACHE, 1
      //       FOR, FILES, ., xml, kml
      //          ,, XMELON, FILE2DB, @<fullPath>, testXMeLon.db
      //       XMELON, CACHE, 0
      //
      // ====== Accessing the data
      //
      // All data is contained in the given xmelon database schema and can be easily accessed using the DEEPDB connections
      // for instance, in order to have a table with filename, path, last tag and data of every data in the database
      // following deep SQL can be used
      //
      //     <xmelon data deep SQL>
      //       DEEPDB, testXMeLon.db, xmelon_data
      //             ,, file fullPath
      //             ,, path pathStr
      //             ,, tag tagStr
      //             ,, value
      //
      // It provides a view of all the contents as a single table and can be useful for example to find any data within all files.
      //
      // The xmelon schema still permits technically another very powerful view of the xml data, that is: a xml path as a table.
      //
      //
      // ====== XMELON schema
      //
      // The XMELON schema consist on the following tables ('xmelon' prefix is default)
      //
      //    table xmelon_files (fileID, timeParse, fullPath)
      //       Multiple files even with different xml schemas might be parsed, this table records
      //       every parsed file
      //
      //    table xmelon_pathDef (patID, parentPatID, pathStr, pathStrNormal, lastNode, level)
      //       Every different xml path that contains final data is recorded in this table
      //
      //    table xmelon_tagDef (tagID, tagStr, tagStrNormal)
      //       Every different tag that contains final data is recorded in this table
      //
      //    table xmelon_data (fileID, dataCnt, patCnt, parentPatCnt, patID, tagID, dataPlace, value)
      //       Every final data is recorded in this table with its associated file, path and tag id's: fileID, patID and tagID
      //
      // Connections between tables (see DEEPDB command) to facilitate the queries are generated as well, specifically
      //
      //    connection name      source table        field          target table      field
      //    -----------------    -----------------   -------------- ----------------  ---------
      //    parentPath           xmelon_pathDef      parentPatID    xmelon_pathDef    patID
      //    file                 xmelon_data         fileID         xmelon_files      fileID
      //    path                 xmelon_data         patID          xmelon_pathDef    patID
      //    tag                  xmelon_data         tagID          xmelon_tagDef     tagID
      //
      // ======= Apendix : building "xml path as a Table" queries from XMELON schema
      //
      // Keeping in mind the xmelon schema given above, we see that
      //
      //    - The table xmelon_pathDef contain directly all the xml paths having data
      //
      //    - The table xmelon_data grouped by patID and tagID give us the information about the "fields"
      //      of each xml path represented by patID, since each tagID can be associated with a field
      //
      //    - Finally we can build a select query against xmelon_data for a specific patID (xml path) containing
      //      some general fields like 'fileID', 'patID', 'patCnt' and 'parentPatCnt' plus following sql expression
      //      for each associated field given by a @<tagID> and its name @<tag_tagStr>
      //             MAX(CASE WHEN tagID = @<tagID> THEN value END) AS c_@<tag_tagStr>
      //
      // This is what the listix format "SQL FOR PATH (pathStr)" from META-GASTONA/utilApp/xmelon/util.lsx does
      // for instance, a select query for the table associated with the xml path "/CATALOG/CD" could be writen as:
      //
      //    <sql for cds>
      //      LSX, SQL FOR PATH (pathStr), "/CATALOG/CD"
      //         , LOAD FORMATS, META-GASTONA/utilApp/xmelon/util.lsx
      //
      // With this technique we are able to form queries for all xml paths contained in xmelon data. Is quite common in xml schemas
      // to have xml path with data containing as well other xml paths (subpaths), thus having a hierarchy of xml paths. In DeepDB
      // language this are connections between the tables (or views) representing the xml paths, this connections can be obtained directly
      // from the table xmelon_pathDef from the pairs 'patID', 'parentPatID'. If we consider the deep select
      //
      //     DEEP DB, SELECT, xmelon_pathDef
      //            ,     , patID
      //            ,     , parentPatID
      //            ,     , pathStr
      //            ,     , pathStrNormal
      //            ,     , parentPath pathStrNormal
      //            ,     , parentPath lastNode
      //
      // for each record where pathStr != '' AND parentPatID != 0, we get a connection defined by two fields
      //
      //    connection name           source table         field            target table                   field
      //    -----------------         -----------------    --------------   ----------------               ---------
      //    @<parentPath_lastNode>    @<pathStrNormal>     fileID           @<parentPath_pathStrNormal>    fileID
      //    @<parentPath_lastNode>    @<pathStrNormal>     parentPatCnt     @<parentPath_pathStrNormal>    patCnt
      //

   <aliases>
      alias

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    4      , //Parse 'xmlSourceFile' and set the result into the database 'dbName' creating the schema if needed
         2   ,    4      , //Enables or disables a cache used to keep loaded tables (tagID and pathID) that accelerates the parsing with multiple files
         3   ,    4      , //Create views and connections (see command DEEPDB) for all the xml paths containing data found in all parsed xml files


   <syntaxParams>
      synIndx, name         , defVal      , desc
         1   , FILE2DB      ,             , //
         1   , xmlSourceFile,             , //Name of text file to be parsed
         1   , targetDbName , (default db), //Database name where to put the result of parsing
         1   , tablePrefix  , xmelon      , //Optional prefix for the result tables ('prefix'_files, 'prefix'_tagDef, 'prefix'_pathDef and 'prefix'_data)

         2   , JSON2DB      ,             , //
         2   , jsonSourceFile,            , //Name of the JSON file to be parsed
         2   , targetDbName , (default db), //Database name where to put the result of parsing
         2   , tablePrefix  , xmelon      , //Optional prefix for the result tables ('prefix'_files, 'prefix'_tagDef, 'prefix'_pathDef and 'prefix'_data)

         3   , ENABLE CACHE ,             , //
         3   , 0 / 1        , 0           , //Set to 1 if want xmelon to use a cache for tagID and pathID tables. In loops it makes the parser to work faster

         4   , CREATE CONTENT VIEWS,      ,
         4   , targetDbName , (default db), //Database name containing the xmelon data obtained on previous XMELON parse operations
         4   , tablePrefix  , xmelon      , //Optional prefix for the result tables ('prefix'_files, 'prefix'_tagDef, 'prefix'_pathDef and 'prefix'_data)

<!         3   , INIT MULTI    ,             , //
<!         3   , targetFileBase,             , //default "parsons" will generate parsons_files.txt parsons_parsons.txt

<!         4   , MULTI        ,             , //
<!         4   , sourceFile   ,             , //Name of text file to be parsed
<!         4   , recordPatternRef,          , //Eva name where the Pattern is to be found (pattern given in an Eva variable)

   <options>
      synIndx, optionName, parameters, defVal, desc
      1      , IGNORE TAGS, "tag, tag, ..",       , //List of tags to ignore, the data found in these tags will pass to the parent (NOTE: This feature is experimental!)

   <examples>
      gastSample
      xmelon parse CD catalog
      xmelon simple sample
      xmelon directory

   <xmelon parse CD catalog>
      //#javaj#
      //
      //   <frames> main, XMELON demo CD catalog, 500, 400
      //
      //   <layout of main>
      //      EVA, 7, 7, 6, 6
      //
      //         , X,
      //       X , tTablePath
      //         , lStatus
      //
      //#data#
      //
      //   <music>
      //      //<?xml version="1.0"?>
      //      //  <shelf>
      //      //         <cd>
      //      //             <title>Blood on the tracks</title>
      //      //             <author>Bob Dylan</author>
      //      //         </cd>
      //      //         <cd>
      //      //             <title>The man who sold the world</title>
      //      //             <author>David Bowie</author>
      //      //             <year>1971</year>
      //      //         </cd>
      //      //         <cd>
      //      //             <title>Dioptria</title>
      //      //             <author>Pau Riba</author>
      //      //             <year>1971</year>
      //      //         </cd>
      //      //  </shelf>
      //
      //
      //   <iPaths visibleColumns> pathStr
      //
      //
      //#listix#
      //
      //   <main>
      //      -->, lStatus data!,, //generating temporary xml ...
      //      VAR=, t1, @<:lsx tmp xml>
      //      GEN, @<t1>, music
      //
      //      -->, lStatus data!,, //parsing xml file onto a xmelon db ...
      //      XMELON, FILE2DB, @<t1>
      //
      //      -->, lStatus data!,, //loading data ...
      //      -->, tTablePath data!, sqlSelect, //@<sql>
      //      -->, lStatus data!,, //done.
      //
      //   <sql>
      //      LSX, GET_SQL_FOR_XMLPATH, /shelf/cd
      //         , LOAD FORMATS, META-GASTONA/utilApp/xmelon/util.lsx

   <xmelon simple sample>
      //#javaj#
      //
      //   <frames> main, XMELON demo, 500, 400
      //
      //   <layout of main>
      //      EVA, 7, 7, 6, 6
      //
      //         ,                   , X
      //         , lXML file to parse, eXMLFile,
      //         , bParse            , -
      //         , lXML Paths found  , -
      //       X , iPaths            , -
      //       X , tTablePath        , -
      //       X , +
      //
      //#data#
      //
      //   <eXMLFile> http://www.w3schools.com/xml/cd_catalog.xml
      //
      //   <iPaths visibleColumns> pathStr
      //
      //
      //#listix#
      //
      //   <-- eXMLFile>
      //       LSX, -- bParse
      //
      //   <-- bParse>
      //      XMELON, FILE2DB, @<eXMLFile>
      //      -->, iPaths data!, sqlSelect, //SELECT * FROM xmelon_pathDef
      //
      //   <-- iPaths>
      //      -->, tTablePath data!, sqlSelect, @<sql>
      //
      //   <sql>
      //      LSX, SQL PATH ASSOCIATED FIELDS (pathID), @<iPaths selected.patID>
      //         , LOAD FORMATS, META-GASTONA/utilApp/xmelon/util.lsx


   <xmelon directory>
      //#javaj#
      //
      //   <frames> main, XMELON directory demo, 500, 400
      //
      //   <layout of main>
      //      EVA, 7, 7, 6, 6
      //
      //         ,                   , X
      //         , lXML directory    , eXMLDir,
      //         , bParse            , -
      //
      //#listix#
      //
      //   <-- eXMLDir>
      //       LSX, -- bParse
      //       LSX, -- bParse
      //
      //   <-- bParse>
      //      XMELON, ENABLE CACHE, 1
      //      LOOP, FILES, @<eXMLDir>, xml
      //          ,, XMELON, FILE2DB, @<fullPath>
      //      XMELON, ENABLE CACHE, 0
      //      XMELON, CONTENT VIEWS
      //      JAVA, gastona.gastona, main, META-GASTONA/utilApp/arces/arces.gast, @<:sys gastona.defaultDB>
      //

#**FIN EVA#
*/

package listix.cmds;

import java.util.*;

import listix.*;
import listix.table.*;
import de.elxala.Eva.*;
import de.elxala.langutil.*;
import de.elxala.langutil.filedir.*;
import de.elxala.db.sqlite.*;
import de.elxala.db.utilEscapeStr;
import de.elxala.parse.xml.*;
import de.elxala.parse.json.*;

import de.elxala.zServices.*;
import de.elxala.mensaka.*;   // for messages start, progress, end


/**
*/
public class cmdXmelon implements commandable
{
   //private static MessageHandle TX_FATALERROR    = new MessageHandle (); // "_lib scanFiles_error"
   private static MessageHandle LIGHT_MSG_START     = null; // new MessageHandle (); // "ledMsg parsons_start"
   private static MessageHandle LIGHT_MSG_PROGRESS  = null; // new MessageHandle (); // "ledMsg parsons_progresss"
   private static MessageHandle LIGHT_MSG_END       = null; // new MessageHandle (); // "ledMsg parsons_end"

   //Note : this variable is just a way to ensure that the static method is called once
   private static boolean sendMessages = initOnce_msgHandles ();

   private saXmelon   theSaXmelon = null;
   private jsonXmelon theJsonMelon = null;
   
   private boolean allowCache = false;

   private int fileID = -1;

   private static boolean initOnce_msgHandles ()
   {
      if (LIGHT_MSG_START == null)
      {
         LIGHT_MSG_START     = new MessageHandle ();
         LIGHT_MSG_PROGRESS  = new MessageHandle ();
         LIGHT_MSG_END       = new MessageHandle ();

         //(o) TODO_parsons Unify ledMsg parsing_start etc for PARSONS and XMELON

         //NOTE! the message should be the same as in parsons, this signal should be general
         //      for parsing activity

         // this messages are not mandatory to be subscribed, the are provided just as internal information of parser command
         Mensaka.declare (null, LIGHT_MSG_START    , "ledMsg parsing_start"      , logServer.LOG_DEBUG_0);
         Mensaka.declare (null, LIGHT_MSG_PROGRESS , "ledMsg parsing_progresss"  , logServer.LOG_DEBUG_0);
         Mensaka.declare (null, LIGHT_MSG_END      , "ledMsg parsing_end"        , logServer.LOG_DEBUG_0);
      }
      return true;
   }

   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String []
      {
          "XMELON",
       };
   }

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts
   */
   public int execute (listix that, Eva commands, int indxComm)
   {
      listixCmdStruct cmd = new listixCmdStruct (that, commands, indxComm);

      String oper        = cmd.getArg(0);
      String fileSource  = cmd.getArg(1);
      String dbName      = cmd.getArg(2);
      String tablePrefix = cmd.getArg(3);
      if (tablePrefix.length () == 0)
         tablePrefix = "xmelon";

      if (dbName.length () == 0)
         dbName = cmd.getListix ().getDefaultDBName ();

      boolean optCache   = cmd.meantConstantString (oper, new String [] { "ENABLECACHE", "CACHE" } );
      boolean optXMLFile2DB = cmd.meantConstantString (oper, new String [] { "FILE2DB", "XML", "XML2DB" } );
      boolean optJSONFile2DB = cmd.meantConstantString (oper, new String [] { "JSON", "JSON2DB" } );
      boolean optCreateViews = cmd.meantConstantString (oper, new String [] { "CREATECONTENTVIEWS", "CREATEVIEWS", "CONTENTVIEWS" } );

      if (!optCache && !optXMLFile2DB && !optJSONFile2DB && !optCreateViews)
      {
         cmd.getLog().err ("XMELON", "operation [" + oper + "] not valid (try one of FILE2DB, XML, JSON, ENABLECACHE or CREATECONTENTVIEWS)");
         return 1;
      }

      if (optCache)
      {
         allowCache = cmd.getArg(1).equals ("1");
         cmd.getLog().dbg (2, "XMELON", "operation [" + oper + "] set it to " + allowCache);
         // reset anyway
         if (theSaXmelon != null)
         {
            theSaXmelon.clearCache ();
         }
         if (theJsonMelon != null)
         {
            theJsonMelon.clearCache ();
         }
         cmd.checkRemainingOptions (true);
         return 1;
      }

      if (optCreateViews)
      {
         dbName      = cmd.getArg(1);
         tablePrefix = cmd.getArg(2);

         cmd.getLog().dbg (2, "XMELON", "CREATE CONTENT VIEWS : dbName [" +  dbName + "] prefix [" + tablePrefix + "]");

         Eva myCommand = new Eva ("internal4XmelonCreateContentViews");
         myCommand.addLine (new EvaLine ("LSX, CREATE VIEWS FOR ALL XML PATHS"));
         myCommand.addLine (new EvaLine ("   , LOAD FORMATS, META-GASTONA/utilApp/xmelon/util.lsx"));
         if (dbName.length () > 0)
            myCommand.addLine (new EvaLine ("   , SET VAR DATA, DBNAME, //" + dbName));
         if (tablePrefix.length () > 0)
            myCommand.addLine (new EvaLine ("   , SET VAR DATA, XMELON_PREFIX, //" + tablePrefix));
         //myCommand.add (new EvaLine ("   , SET VAR DATA, BREAK_LINE, \"\n\""));

         that.doFormat (myCommand);

         cmd.checkRemainingOptions (true);
         return 1;
      }

      if (cmd.getLog().isDebugging (2))
         cmd.getLog().dbg (2, "XMELON", "execute with : oper [" + oper + "] fileSource [" + fileSource + "] dbName [" +  dbName + "] prefix [" + tablePrefix + "]");
      if (fileSource.length () == 0)
      {
         that.log ().err ("XMELON", "No file to scan given!");
         return 1;
      }

      /*
         05.11.2011 01:19
         NOTE: THIS OPTION IS EXPERIMENTAL!!! Probably only supports one sub level
               It is a workaround to superflous tag's like <b> or <ref>
               (doxygen uses <ref> sometimes in the xml output but since we pass the
                data to sqlite, references can be found easily)


         Option IGNORE TAGS, tag1, tag2, ...

         for example IGNORE TAGS, b, ref

            <some>
               <name><b>my name</b><name>
            </some>
            <some>
               <name><ref link="xx">my second name</ref><name>
            </some>

         will produce the same result as if the tags <b> and <ref> weren't present, that is

            <some>
               <name>my name<name>
            </some>
            <some>
               <name>my second name<name>
            </some>


      */
      String [] ignoreSubTag = cmd.takeOptionParameters(new String [] {"IGNORETAGS", "IGNORE" }, true);
      List ignoreSubTagList = new Vector ();
      if (ignoreSubTag != null && ignoreSubTag.length > 0)
         for (int ii = 0; ii < ignoreSubTag.length; ii ++)
            ignoreSubTagList.add (ignoreSubTag[ii]);

      if (optXMLFile2DB)
      {
         // Important to keep theSaXmelon alive to take profit of its
         // cache habilities (e.g. when parsing multiple files calling the command XMELON
         // continously)
         //
         if (theSaXmelon == null)
         {
            theSaXmelon = new saXmelon ();
         }

         if (!allowCache)
         {
            theSaXmelon.clearCache ();
         }

         theSaXmelon.parseFile (fileSource, dbName, tablePrefix, allowCache, ignoreSubTagList);
      }
      if (optJSONFile2DB)
      {
         if (theJsonMelon == null)
         {
            theJsonMelon = new jsonXmelon ();
         }

         if (!allowCache)
         {
            theJsonMelon.clearCache ();
         }

         theJsonMelon.parseFile (fileSource, dbName, tablePrefix, allowCache, ignoreSubTagList);
      }
            
      if (sendMessages) Mensaka.sendPacket (LIGHT_MSG_END, null); // ... )))

      cmd.checkRemainingOptions (true);
      return 1;
   }
}
