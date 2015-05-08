/* Generator: Checksum generation methods.
   $Id: Generator.java,v 1.12 2003/07/20 04:26:13 rsdio Exp $

   Copyright (C) 2003  Casey Marshall <rsdio@metastatic.org>

   This file is a part of Jarsync.

   Jarsync is free software; you can redistribute it and/or modify it
   under the terms of the GNU General Public License as published by the
   Free Software Foundation; either version 2 of the License, or (at
   your option) any later version.

   Jarsync is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Jarsync; if not, write to the

      Free Software Foundation, Inc.,
      59 Temple Place, Suite 330,
      Boston, MA  02111-1307
      USA

   Linking Jarsync statically or dynamically with other modules is
   making a combined work based on Jarsync.  Thus, the terms and
   conditions of the GNU General Public License cover the whole
   combination.

   As a special exception, the copyright holders of Jarsync give you
   permission to link Jarsync with independent modules to produce an
   executable, regardless of the license terms of these independent
   modules, and to copy and distribute the resulting executable under
   terms of your choice, provided that you also meet, for each linked
   independent module, the terms and conditions of the license of that
   module.  An independent module is a module which is not derived from
   or based on Jarsync.  If you modify Jarsync, you may extend this
   exception to your version of it, but you are not obligated to do so.
   If you do not wish to do so, delete this exception statement from
   your version.  */


package org.metastatic.rsync;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Adler32;

/**
 * Checksum generation methods.
 *
 * @version $Revision: 1.12 $
 */
public class Generator {

   // Constants and variables.
   // ------------------------------------------------------------------------

   /**
    * Our configuration. Contains such things as our rolling checksum
    * and message digest.
    */
   protected final Configuration config;

   // Constructors.
   // ------------------------------------------------------------------------

   public Generator(Configuration config) {
      this.config = config;
   }

   // Instance methods.
   // ------------------------------------------------------------------------

   /**
    * Generate checksums over an entire byte array, with a base offset
    * of 0.
    *
    * @param buf The byte buffer to checksum.
    * @return A {@link java.util.List} of {@link ChecksumPair}s
    *    generated from the array.
    * @see #generateSums(byte[],int,int,long)
    */
   public List generateSums(byte[] buf) {
      return generateSums(buf, 0, buf.length, 0);
   }

   /**
    * Generate checksums over a portion of a byte array, with a base
    * offset of 0.
    *
    * @param buf The byte array to checksum.
    * @param off The offset in <code>buf</code> to begin.
    * @param len The number of bytes to checksum.
    * @return A {@link java.util.List} of {@link ChecksumPair}s
    *    generated from the array.
    * @see #generateSums(byte[],int,int,long)
    */
   public List generateSums(byte[] buf, int off, int len) {
      return generateSums(buf, off, len, 0);
   }

   /**
    * Generate checksums over an entire byte array, with a specified
    * base offset. This <code>baseOffset</code> is added to the offset
    * stored in each {@link ChecksumPair}.
    *
    * @param buf        The byte array to checksum.
    * @param baseOffset The offset from whence this byte array came.
    * @return A {@link java.util.List} of {@link ChecksumPair}s
    *    generated from the array.
    * @see #generateSums(byte[],int,int,long)
    */
   public List generateSums(byte[] buf, long baseOffset) {
      return generateSums(buf, 0, buf.length, baseOffset);
   }

   /**
    * Generate checksums over a portion of abyte array, with a specified
    * base offset. This <code>baseOffset</code> is added to the offset
    * stored in each {@link ChecksumPair}.
    *
    * @param buf        The byte array to checksum.
    * @param off        From whence in <code>buf</code> to start.
    * @param len        The number of bytes to check in
    *                   <code>buf</code>.
    * @param baseOffset The offset from whence this byte array came.
    * @return A {@link java.util.List} of {@link ChecksumPair}s
    *    generated from the array.
    */
   public List generateSums(byte[] buf, int off, int len, long baseOffset) {
      int count = (len+(config.blockLength-1)) / config.blockLength;
      int remainder = len % config.blockLength;
      int offset = off;
      List sums = new ArrayList(count);

      for (int i = 0; i < count; i++) {
         int n = Math.min(len, config.blockLength);
         ChecksumPair pair = generateSum(buf, offset, n, offset+baseOffset);
         pair.seq = i;

         sums.add(pair);
         len -= n;
         offset += n;
      }

      return sums;
   }

   /**
    * Generate checksums for an entire file.
    *
    * @param f The {@link java.io.File} to checksum.
    * @return A {@link java.util.List} of {@link ChecksumPair}s
    *    generated from the file.
    * @throws java.io.IOException if <code>f</code> cannot be read from.
    */
   public List generateSums(File f) throws IOException {
      long len = f.length();
      int count = (int) ((len+(config.blockLength+1)) / config.blockLength);
      long offset = 0;
      FileInputStream fin = new FileInputStream(f);
      List sums = new ArrayList(count);
      int n = (int) Math.min(len, config.blockLength);
      byte[] buf = new byte[n];

      for (int i = 0; i < count; i++) {
         int l = fin.read(buf, 0, n);
         if (l == -1) break;
         ChecksumPair pair = generateSum(buf, 0, Math.min(l, n), offset);
         pair.seq = i;

         sums.add(pair);
         len -= n;
         offset += n;
         n = (int) Math.min(len, config.blockLength);
      }

      fin.close();
      return sums;
   }

   /**
    * Generate checksums for an InputStream.
    *
    * @param in The {@link java.io.InputStream} to checksum.
    * @return A {@link java.util.List} of {@link ChecksumPair}s
    *    generated from the bytes read.
    * @throws java.io.IOException if reading fails.
    */
   public List generateSums(InputStream in) throws IOException {
      List sums = null;
      byte[] buf = new byte[config.blockLength*config.blockLength];
      long offset = 0;
      int len = 0;

      while ((len = in.read(buf)) != -1) {
         if (sums == null) {
            sums = generateSums(buf, 0, len, offset);
         } else {
            sums.addAll(generateSums(buf, 0, len, offset));
         }
         offset += len;
      }

      return sums;
   }

   /**
    * Generate a sum pair for an entire byte array.
    *
    * @param buf The byte array to checksum.
    * @param fileOffset The offset in the original file from whence
    *    this block came.
    * @return A {@link ChecksumPair} for this byte array.
    */
   public ChecksumPair generateSum(byte[] buf, long fileOffset) {
      return generateSum(buf, 0, buf.length, fileOffset);
   }

   /**
    * Generate a sum pair for a portion of a byte array.
    *
    * @param buf The byte array to checksum.
    * @param off Where in <code>buf</code> to start.
    * @param len How many bytes to checksum.
    * @param fileOffset The original offset of this byte array.
    * @return A {@link ChecksumPair} for this byte array.
    */
   public ChecksumPair
   generateSum(byte[] buf, int off, int len, long fileOffset) {
      ChecksumPair p = new ChecksumPair();
//      Adler32 alder32 = new Adler32();
//      alder32.update(buf, off, len);
      config.weakSum.check(buf, off, len);
      config.strongSum.update(buf, off, len);
      if (config.checksumSeed != null) {
         config.strongSum.update(config.checksumSeed, 0,
            config.checksumSeed.length);
      }
      p.weak = config.weakSum.getValue();
//      long weakchecksum = alder32.getValue();
//      p.weak=(int)weakchecksum;
      p.strong = new byte[config.strongSumLength];
      System.arraycopy(config.strongSum.digest(), 0, p.strong, 0,
         p.strong.length);
      p.offset = fileOffset;
      p.length = len;
      return p;
   }
}
