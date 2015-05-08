/* vim:set softtabstop=3 shiftwidth=3 tabstop=3 expandtab tw=72:
   $Id: Statistics.java,v 1.3 2003/04/04 16:10:31 rsdio Exp $

   Statistics -- Rsync connection statistics.
   Copyright (C) 2003  Casey Marshall <rsdio@metastatic.org>

   This file is a part of Jarsync.

   Jarsync is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.

   Jarsync is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Jarsync; if not, write to the

      Free Software Foundation, Inc.,
      59 Temple Place, Suite 330,
      Boston, MA  02111-1307
      USA  */

/*
 * Based on rsync-2.5.5.
 * 
 * Rsync Copyright (C) 1992-2001 Andrew Tridgell
 *                     1996 Paul Mackerras
 *                     2001, 2002 Martin Pool
 *                     and others.
 */

package org.metastatic.rsync.v2;

public final class Statistics {

   // Fields.
   // -----------------------------------------------------------------------

   public long total_size;

   public long total_transferred_size;

   public long total_written;

   public long total_read;

   public long literal_data;

   public long matched_data;

   public int flist_size;

   public int num_files;

   public int num_transferred_files;

   // Default 0-arguments constructor.
}
