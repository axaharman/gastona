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
   //(o) WelcomeGastona_source_listix_command VALUE OF

   ========================================================================================
   ================ documentation for javajCatalog.gast ===================================
   ========================================================================================

   This embedded EvaUnit describe the documentation for this listix command. Basically contains
   the syntaxes, options and examples for the listix commnad.

#gastonaDoc#

   <docType>    listix_command
   <name>       VALUE OF
   <groupInfo>  lang_variables
   <javaClass>  listix.cmds.cmdValueOf
   <importance> 2
   <desc>       //Print out the value of the given variable name

   <help>
      //
      // Prints out(*) the value of the given variable name. Used with a plain variable name
      // it will return its value which is actually not very interesting but if want
      // to form the variable name using other variable it is the only way to retrieve
      // the value of this variable. Note that something like "@<text of @<myVar>>" is not legal
      // and would produce an error, in this case we can use "VALUE OF" as follows:
      //
      //       VALUE OF, text of @<myVar>
      //
      // (*) On the current listix target (e.g. console, variable solving etc)

   <aliases>
      alias
      VALUE

   <syntaxHeader>
      synIndx, importance, desc
         1   ,    1      , //Returns the value of the given Eva variable

   <syntaxParams>
      synIndx, name         , defVal, desc
         1   , refToSolve   ,       , //Variable name given directly or through a listix expresion which value has to be retrieved

   <options>
      synIndx, optionName  , parameters, defVal, desc

   <examples>
      gastSample

      valueof example
      valueof example 2

   <valueof example>
      //#javaj#
      //
      //   <frames> oConsole, listix command VALUE OF example
      //
      //#listix#
      //
      //   <language> spanish
      //
      //   <main0>
      //       VALUE OF, Hello @<language>
      //
      //   <Hello spanish>
      //       //Hola!
      //       //
      //
      //   <Hello german>
      //       //Hallo!
      //       //
      //
      //   <Hello english>
      //       //Hello!
      //       //

   <valueof example 2>
      //#javaj#
      //
      //   <frames> F, listix command VALUE OF example 2
      //
      //   <layout of F>
      //       EVA, 10, 10, 7, 7
      //
      //          ,         , X
      //        X , tRecords, -
      //          , cField  , eValue
      //
      //#data#
      //
      //   <tRecords>
      //       id, Name     , City     ,  WhatIs
      //       71, Abelardo , Plasencia,  Casino
      //       61, Renate   , G�ttingen,  B�ckerei
      //       81, Sergej   , Riga     ,  Buro
      //       16, Suelen   , Nagoya   ,  Suzuki Partner
      //
      //   <cField visibleColumns> say
      //   <cField>
      //       fieldName, say
      //       id       , has the id
      //       Name     , it is called
      //       City     , lies on
      //       WhatIs   , it is a
      //
      //#listix#
      //
      //   <-- cField> @<show field>
      //   <-- tRecords> @<show field>
      //
      //   <show field>
      //       CHECK, VAR, tRecords selected.id
      //       SET VAR, eValue, @<theValue>
      //       MSG, eValue data!
      //
      //   <theValue>
      //       VALUE OF, tRecords selected.@<cField selected.fieldName>



#listix#

   <-- cField> @<dilo>
	<-- tRecords> @<dilo>

   <dilo>
       <!CHECK, VAR, tRecords selected
       SET VAR, eValue, @<cField selected.say> @<theValue>
		 MSG, eValue data!

   <theValue>
       VALUE OF, tRecords selected.@<cField selected.fieldName>


#**FIN_EVA#

*/

package listix.cmds;

import listix.*;
import de.elxala.Eva.*;

public class cmdValueOf implements commandable
{
   /**
      get all the different names that the command can have
   */
   public String [] getNames ()
   {
      return new String [] {
          "VALUE OF",
          "VALUE",
       };
   }

   /**
      Execute the commnad and returns how many rows of commandEva
      the command had.

         that           : the environment where the command is called
         commandEva     : the whole command Eva
         indxCommandEva : index of commandEva where the commnad starts

      <valorDelCampo>
         VALUE OF, @<campo>

   */
   public int execute (listix that, Eva commandEva, int indxComm)
   {
      String refToSolve = that.solveStrAsString (commandEva.getValue (indxComm, 1));

      // System.out.println ("me vengo refiriendo a que [" + valor + "]");
      // System.exit (0);

      // retFormat = solveLsxFormatAsEva (valor);
      // valor = retFormat.getValue ();
      if (! that.printLsxFormat (refToSolve))
      {
         that.log().err ("VALUEOF", "listix format [" + refToSolve + "] not found while running " + commandEva.getName ());
      }

      return 1;
   }
}