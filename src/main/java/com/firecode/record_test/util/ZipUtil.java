package com.firecode.record_test.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ZipUtil {

	/**
	 * 解压客户端发来的程序
	 *
	 * @param depressData
	 * @return
	 * @throws Exception
	 */
	public static byte[] decompress(byte[] depressData) throws Exception {

		ByteArrayInputStream is = new ByteArrayInputStream(depressData);
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		GZIPInputStream gis = new GZIPInputStream(is);

		int count;
		byte data[] = new byte[1024];
		while ((count = gis.read(data, 0, 1024)) != -1) {
			os.write(data, 0, count);
		}
		gis.close();
		depressData = os.toByteArray();
		os.flush();
		os.close();
		is.close();
		return depressData;
	}

	public static byte[] gZip(byte[] data) {
		byte[] b = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(bos);
			gzip.write(data);
			gzip.finish();
			gzip.close();
			b = bos.toByteArray();
			bos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
}
