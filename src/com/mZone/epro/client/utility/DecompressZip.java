package com.mZone.epro.client.utility;

/**
 * 
 * @author HUY
 * @Created Date: 2013/12/23
 * @Unpack ZIP file
 */

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import android.util.Log;

public class DecompressZip {
	private static final int BUFFER_SIZE=8192;

	private String _zipFile;
	private String _location;
	private byte[] _buffer;

	/**
	 * Constructor.
	 * 
	 * @param zipFile		Fully-qualified path to .zip file
	 * @param location		Fully-qualified path to folder where files should be written.
	 * 						Path must have a trailing slash.
	 */
	public DecompressZip(String zipFile, String location) {
		_zipFile = zipFile;
		_location = location;
		_buffer = new byte[BUFFER_SIZE];
		dirChecker("");
	}

	public boolean unzip() {
		FileInputStream fin = null;
		ZipInputStream zin = null;
		OutputStream fout = null;
		
		File outputDir = new File(_location);
		File tmp = null;
		
		//Check zip file corrupted or not
	    ZipFile zipfile = null;
	    try {
	        zipfile = new ZipFile(_zipFile);
	    } catch (ZipException e) {
	        return false;
	    } catch (IOException e) {
	        return false;
	    } finally {
	        try {
	            if (zipfile != null) {
	                zipfile.close();
	                zipfile = null;
	            }
	        } catch (IOException e) {
	        }
	    }
	    ////////////////////////////////////////
		
		try {
			fin = new FileInputStream(_zipFile);
			zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null) {
				Log.d("Decompress", "Unzipping " + ze.getName());

				if (ze.isDirectory()) {
					dirChecker(ze.getName());
				} else {
					tmp = File.createTempFile( "decomp", ".tmp", outputDir );
					fout = new BufferedOutputStream(new FileOutputStream(tmp));
					DownloadFile.copyStream( zin, fout, _buffer, BUFFER_SIZE );
					zin.closeEntry();
					fout.close();
					fout = null;
					tmp.renameTo( new File(_location + ze.getName()) );
					tmp = null; 
				}
			}
			zin.close();
			zin = null;
		} catch (IOException e) {
			return false;
		} finally {
			if ( tmp != null  ) { try { tmp.delete();     } catch (Exception ignore) {return false;} }
			if ( fout != null ) { try { fout.close(); 	  } catch (Exception ignore) {return false;} }
			if ( zin != null  ) { try { zin.closeEntry(); } catch (Exception ignore) {return false;} }
			if ( fin != null  ) { try { fin.close(); 	  } catch (Exception ignore) {return false;} }
		}
		return true;
	}

	private void dirChecker(String dir) {
		File f = new File(_location + dir);

		if (!f.isDirectory()) {
			f.mkdirs();
		}
	}
}