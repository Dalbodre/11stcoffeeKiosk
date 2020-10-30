package sb.yoon.kiosk.tools;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Decompress {
    private InputStream _zipFileStream;  //저장된 zip 파일 위치
    private String _location; //압축을 풀 위치

    public Decompress(InputStream zipFileStream, String location) {
        _zipFileStream = zipFileStream;
        _location = location;

        _dirChecker(_location); //폴더를 만들기 위한 함수로 아래에 정의 되어 있습니다.
    }

    public void unzip() {
        try {
            ZipInputStream zin = new ZipInputStream(_zipFileStream);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                Log.v("Decompress", "Unzipping " + ze.getName());
                if (ze.isDirectory()) {
                    _dirChecker(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(_location + ze.getName());
                    BufferedInputStream in = new BufferedInputStream(zin);
                    BufferedOutputStream out = new BufferedOutputStream(fout);
                    byte[] b = new byte[1024];
                    int n;
                    while ((n = in.read(b, 0, 1024)) >= 0) {
                        out.write(b, 0, n);
                    }
                    zin.closeEntry();
                    fout.close();
                }
            }
            zin.close();
        } catch (Exception e) {
            Log.e("Decompress", "unzip", e);
        }
    }


    //변수 location에 저장된 directory의 폴더를 만듭니다.
    private void _dirChecker(String dir) {
        File f = new File(_location + dir);

        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }
}