package br.uefs.utils;

import br.uefs.exceptions.NoSuchPropertyException;

import java.util.List;

public class PropertiesParser {
    private List<String> properties;

    public PropertiesParser(List<String> properties) {
        this.properties = properties;
    }

    public int parseInt(String propertie) {
        int indexProperty = properties.indexOf(propertie);
        if (indexProperty != -1) {
            return Integer.parseInt(properties.get(indexProperty + 1));
        } else {
            throw new NoSuchPropertyException(propertie + " property not found");
        }
    }

    public float parserFloat(String propertie) {
        int indexProperty = properties.indexOf("-t");
        if (indexProperty != -1) {
            return Float.parseFloat(properties.get(indexProperty + 1));
        } else {
            throw new NoSuchPropertyException("-t property not found");
        }
    }

    public String parseString(String propertie) {
        int indexProperty = properties.indexOf(propertie);
        if (indexProperty != -1) {
            return properties.get(indexProperty + 1);
        } else {
            throw new NoSuchPropertyException(propertie + " property not found");
        }
    }

    public int[] parseIntArray(String propertie) {
        int indexProperty = properties.indexOf(propertie);
        if (indexProperty != -1) {
            String[] arrayAux = properties.get(indexProperty + 1).replace("[", "").replace("]", "").split(",");
            int[] array = new int[arrayAux.length];
            for (int i = 0; i < arrayAux.length; i++) {
                array[i] = Integer.parseInt(arrayAux[i]);
            }
            return array;
        } else {
            throw new NoSuchPropertyException(propertie + " property not found");
        }
    }
}