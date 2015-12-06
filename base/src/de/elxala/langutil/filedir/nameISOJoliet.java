/*
java package de.elxala.Eva (see EvaFormat.PDF)
Copyright (C) 2005  Alejandro Xalabarder Aulet

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package de.elxala.langutil.filedir;

/**   ======== de.elxala.langutil.filedir ==========================================
   Alejandro Xalabarder
*/

import java.io.*;
import de.elxala.zServices.*;
import de.elxala.langutil.*;

/**
   class naming
   @author Alejandro Xalabarder Aulet
   @date   2010

   Utilities with names
*/
public class naming
{

   /**
      for instance, when burning a CD with files
      the name of the files has to be ISO 9660-Joliet

      file name length <= 64
      file name do not contain strange characters (like ';' typically when saving internet pages with IExplorer)
   */
   public static String toNameISO_9660Joliet (String fileName)
   {
      String ss = fileName.substring (0, 60);

      for (int ii = 0; ii < ss.length (); ii ++)
      {
         char ica = ss.charAt (ii);
         if ((ica >= '0' && ica <= '9') ||
             (ica >= 'A' && ica <= 'Z') ||
             (ica >= 'a' && ica <= 'z')) continue;
         ss = ss.replace (ica, '_');
      }
      return ss;
   }

   //ensure a path name is ISO 9660 Joliet
   public static String ensurePathJoliet (String thePath)
   {
      if (thePath.length () == 0) return "";

      // ensure we have only one path separator (/)
      thePath = thePath.replace ('\\', '/');

      String [] subpaths = Cadena.simpleToArray (thePath, "/");
      String ss = "";
      int ii = 0;

      if (subpaths.length > 0)
      {
         // for each except the last one!
         for (ii = 0; (ii+1) < subpaths.length; ii ++)
         {
            ss += toNameISO_9660Joliet (subpaths[ii]);
            ss += "/";
         }
         ss += toNameISO_9660Joliet (subpaths[ii]);
      }

      return ss;
   }
}