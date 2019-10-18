package com.talkweb.basecomp.common.util;

import org.apache.commons.compress.archivers.zip.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.List;

public class ZipUtil {

	public static void zip(OutputStream out, List<String> names, List<String> files) throws Exception {
		BufferedOutputStream bOut = null;
		ZipArchiveOutputStream tOut = null;
		try {
			bOut = new BufferedOutputStream(out);
			tOut = new ZipArchiveOutputStream(bOut);
			int index = 0;
			for (String f : files) {
				addFileToZip(tOut, f, names == null ? null : names.get(index++));
			}
		} finally {
			closeStream(tOut);
			IOUtils.closeQuietly(bOut);
		}
	}

	public static void zip(OutputStream out, String name, String file) throws Exception {
		BufferedOutputStream bOut = null;
		ZipArchiveOutputStream tOut = null;
		try {
			bOut = new BufferedOutputStream(out);
			tOut = new ZipArchiveOutputStream(bOut);
			addFileToZip(tOut, file, name);
		} finally {
			closeStream(tOut);
			IOUtils.closeQuietly(bOut);
		}
	}

	private static void addFileToZip(ZipArchiveOutputStream zOut, String f, String name) throws Exception {
		if (name == null) {
			if (f == null) return;
			name = FilenameUtils.getName(f);
		} else {
			name = name.replaceAll(" ", "");
		}
		ZipArchiveEntry zipEntry = new ZipArchiveEntry(name);
		zOut.putArchiveEntry(zipEntry);
		//zOut.write(FileUtil.urlToBytes(f));
		IOUtils.copy(new URL(f.replaceAll(" ", "%20")).openStream(), zOut);
		zOut.closeArchiveEntry();
	}

	private static void closeStream(ZipArchiveOutputStream tOut) {
		try {
			tOut.finish();
			IOUtils.closeQuietly(tOut);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
