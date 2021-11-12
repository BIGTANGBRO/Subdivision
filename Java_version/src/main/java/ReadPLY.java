/*
 * ReadPLY.java
 * Copyright 2021 Qunhe Tech, all rights reserved.
 * Qunhe PROPRIETARY/CONFIDENTIAL, any form of usage is subject to approval.
 */

import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.PlyReaderFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tangshao
 */
public class ReadPLY {
    public static List<Vector3d> read(PlyReaderFile reader) throws IOException {
        ElementReader elementReader = reader.nextElementReader();
        List<Vector3d> coords = new ArrayList<>();
        while (elementReader != null) {

            Element element = elementReader.readElement();
            while (element != null) {
                if ("vertex".equals(element.getType().getName())) {

                }
                // get next
                element = elementReader.readElement();
            }
            elementReader.close();
            elementReader = reader.nextElementReader();
        }
        return coords;
    }

    public static void main(String[] args) throws IOException {
        String fileName = "C:\\Users\\msipc\\Downloads\\model lib\\bunny\\reconstruction\\bunny_zipper_res4.ply";
        InputStream in = new FileInputStream(fileName);
        PlyReaderFile reader = new PlyReaderFile(in);
    }
}
