package org.sf.jlaunchpad.util;

import java.nio.channels.ReadableByteChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.Channels;
import java.nio.ByteBuffer;
import java.util.jar.Manifest;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;

/**
 * This is the class for holding file utilities.
 *
 * @author Alexander Shvets
 * @version 1.0 05/14/2004
 */
public final class FileUtil {

  /**
   * Copies the stream to the temporary file.
   *
   * @param is   the input stream
   * @param file the file
   * @return the temporary file
   * @throws IOException I/O Exception
   */
  public static File copyToFile(final InputStream is, final File file)
          throws IOException {
    FileOutputStream fos = null;
    ReadableByteChannel source = null;
    FileChannel target = null;

    try {
      fos = new FileOutputStream(file);

      source = Channels.newChannel(is);
      target = fos.getChannel();

      final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);

      while (source.read(buffer) != -1) {
        // prepare the buffer to be drained
        buffer.flip();
        // make sure the buffer was fully drained.
        while (buffer.hasRemaining()) {
          target.write(buffer);
        }

        // make the buffer empty, ready for filling
        buffer.clear();
      }
    }
    finally {
      if (source != null) {
        source.close();
      }

      if (target != null) {
        target.close();
      }

      if (fos != null) {
        fos.close();
      }
    }

    return file;
  }

  /**
   * Copies the stream to the temporary file.
   *
   * @param is     the input stream
   * @param prefix the prefix part of the file name
   * @param suffix the suffix part of the file name
   * @return the temporary file
   * @throws java.io.IOException I/O Exception
   */
  public static File copyToTempFile(final InputStream is, final String prefix, final String suffix)
          throws IOException {
    return copyToFile(is, File.createTempFile(prefix, suffix));
  }

  /**
   * Gets the manifest object from jar file.
   *
   * @param jarFileName the jar file name
   * @return the manifest object
   * @throws IOException I/O Exception
   */
  public static Manifest getManifest(final String jarFileName) throws IOException {
    final JarFile jarFile = new JarFile(jarFileName);

    final Manifest manifest = getManifest(jarFile);

    jarFile.close();

    return manifest;
  }

  /**
   * Gets the manifest object from jar file.
   *
   * @param jarFile the jar file
   * @return the manifest object
   * @throws IOException I/O Exception
   */
  public static Manifest getManifest(final JarFile jarFile) throws IOException {
    Manifest manifest = null;

    ZipEntry zipEntry = jarFile.getEntry("META-INF/MANIFEST.MF");

    if (zipEntry == null) {
      zipEntry = jarFile.getEntry("meta-inf/manifest.mf");
    }

    if (zipEntry != null) {
      final InputStream is = jarFile.getInputStream(zipEntry);

      manifest = new Manifest(is);

      is.close();
    }

    return manifest;
  }

  /**
   * Converst input stream into the byte array.
   *
   * @param is the input stream
   * @return the byte array
   * @throws IOException the I/O exception
   */
  public static byte[] getStreamAsBytes(final InputStream is) throws IOException {
    BufferedInputStream bis = null;
    ByteArrayOutputStream baos = null;

    try {
      bis = new BufferedInputStream(is);
      baos = new ByteArrayOutputStream();

      final byte[] buffer = new byte[2048];

      boolean ok = false;
      while (!ok) {
        final int n = bis.read(buffer);

        if (n == -1) {
          ok = true;
        } else {
          baos.write(buffer, 0, n);
        }
      }
    }
    finally {
      if (bis != null) {
        bis.close();
      }

      if (baos != null) {
        baos.close();
      }
    }

    return baos.toByteArray();
  }

  /**
   * Gets the Ant project file located inside jar file.
   *
   * @param jarFileName the name of jar file
   * @param location the location
   * @param prefix the prefic
   * @param suffix the suffix
   * @return Ant project file
   * @throws IOException I/O Exception
   */
  public static File getFileFromArchive(final String jarFileName, final String location,
                                        final String prefix, final String suffix) throws IOException {
    File projectFile = null;

    final JarFile jarFile = new JarFile(jarFileName);

    final ZipEntry zipEntry = jarFile.getEntry(location);

    if (zipEntry != null) {
      projectFile =
              FileUtil.copyToTempFile(jarFile.getInputStream(zipEntry), prefix, suffix);
    }

    return projectFile;
  }

  /**
   * Conveinience method that returns the "dot" extension for the given file.
   *
   * @param file the file
   * @return the extension
   */
  public static String getExtension(File file) {
    if(file != null) {
      return getExtension(file.getName());
    }

    return null;
  }

  /**
   * Conveinience method that returns the "dot" extension for the given file.
   *
   * @param fileName the file name
   * @return the extension
   */
  public static String getExtension(String fileName) {
    String extension = null;

    int pos = fileName.lastIndexOf('.');

    if(pos > 0 && pos < fileName.length()-1) {
      extension = fileName.substring(pos+1).toLowerCase();
    }

    return extension;
  }
  
}
