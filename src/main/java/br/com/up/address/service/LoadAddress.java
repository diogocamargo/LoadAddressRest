package br.com.up.address.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Properties;

/**
 * Load the street address by latitude and longitude
 * Created by diogo on 13/11/2016.
 */
public class LoadAddress {
    private static final int INDEX_PLACA = 0;
    private static final int INDEX_ENGINE = 1;
    private static final int INDEX_LAT = 2;
    private static final int INDEX_LNG = 3;
    private static final int INDEX_COUNTRY = 4;
    private static final int INDEX_STREET = 0;
    private static final int INDEX_STATE = 3;
    private static final int INDEX_CITY = 2;

    //Properties
    private static String key;
    private static String file;
    private static Properties prop = new Properties();

    /**
     * Load properties
     */
    static {
        try {
            String propFileName = "properties.properties";
            InputStream in = LoadAddress.class.getResourceAsStream(propFileName);
            if (in != null) {
                prop.load(in);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            key = prop.getProperty("key");
            file = prop.getProperty("file");
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert the parameters: PLATE ENGINE LAT LNG
     *
     * @param args
     */

    public static void main(String args[]) {
        if (args == null || args.length < 4) {
            System.out.println("Insert the parameters: PLATE ENGINE LAT LNG");
            return;
        }

        if (key == null || "".equals(key)) {
            System.out.println("Api Key not found");
            return;
        }

        GeoApiContext context = new GeoApiContext().setApiKey(key);
        GeocodingResult[] results = new GeocodingResult[0];
        try {
            LatLng latLng = new LatLng(Double.valueOf(args[INDEX_LAT]), Double.valueOf(args[INDEX_LNG]));

            results = GeocodingApi.reverseGeocode(context, latLng).await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Arrays.toString(results[0].addressComponents));
        if (results == null || results.length <=0) {
            System.out.println("No results found");
            return;
        }
        writeFile(results[0].addressComponents,args);
    }


    private static void writeFile(final AddressComponent[] addressComponents, String[] args) {
        try {
            //Append string
            StringBuilder sb = new StringBuilder();

            if (Files.notExists(Paths.get(file))){
                Files.createFile(Paths.get(file));
            } else {
                sb.append("\n");
            }
            sb.append(args[INDEX_PLACA]);
            sb.append("|");
            sb.append(args[INDEX_ENGINE]);
            sb.append("|");
            sb.append(addressComponents[INDEX_COUNTRY].shortName);
            sb.append("|");
            sb.append(addressComponents[INDEX_STREET].longName);
            sb.append("|");
            sb.append(args[INDEX_LAT]);
            sb.append("|");
            sb.append(args[INDEX_LNG]);
            sb.append("|");
            sb.append(addressComponents[INDEX_STATE].shortName);
            sb.append("|");
            sb.append(addressComponents[INDEX_CITY].shortName);

            Files.write(Paths.get(file), sb.toString().getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }
    private static void writeFileBuffered(final String[] addressComponents) {
        try(FileWriter fw = new FileWriter("outfilename", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println("the text");
            //more code
            out.println("more text");
            //more code
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }
}
