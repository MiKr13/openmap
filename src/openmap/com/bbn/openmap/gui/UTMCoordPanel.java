// **********************************************************************
// 
// <copyright>
// 
//  BBN Technologies
//  10 Moulton Street
//  Cambridge, MA 02138
//  (617) 873-8000
// 
//  Copyright (C) BBNT Solutions LLC. All rights reserved.
// 
// </copyright>
// **********************************************************************
// 
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/gui/UTMCoordPanel.java,v $
// $RCSfile: UTMCoordPanel.java,v $
// $Revision: 1.7 $
// $Date: 2005/08/09 17:49:51 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.Serializable;
import javax.swing.*;
import javax.swing.border.*;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.event.CenterSupport;
import com.bbn.openmap.proj.coords.UTMPoint;
import com.bbn.openmap.util.Debug;
import com.bbn.openmap.I18n;

/**
 * UTMCoordPanel is a simple gui with entry boxes and labels for Zone
 * number and letters, and easting and northing representation of
 * latitude and longitude. It sets the center of a map with the
 * entered coordinates by firing CenterEvents.
 */
public class UTMCoordPanel extends CoordPanel implements Serializable {

    protected transient JTextField zoneLetter, zoneNumber, easting, northing;

    /**
     * Creates the panel.
     */
    public UTMCoordPanel() {
        super();
    }

    /**
     * Creates the panel.
     */
    public UTMCoordPanel(CenterSupport support) {
        super(support);
    }

    /**
     * Creates and adds the labels and entry fields for latitude and
     * longitude
     */
    protected void makeWidgets() {
        String locText;
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        setLayout(gridbag);
        locText = i18n.get(UTMCoordPanel.class,
                "border",
                "Zone Number|Hemisphere|Easting|Northing");
        setBorder(new TitledBorder(new EtchedBorder(), locText));

        locText = i18n.get(UTMCoordPanel.class, "utmLabel", "UTM: ");
        JLabel utmLabel = new JLabel(locText);
        c.gridx = 0;
        gridbag.setConstraints(utmLabel, c);
        add(utmLabel);

        c.gridx = GridBagConstraints.RELATIVE;
        zoneNumber = new JTextField(3);
        zoneNumber.setToolTipText(i18n.get(UTMCoordPanel.class,
                "zone",
                I18n.TOOLTIP,
                "Zone Number: 0-60"));
        gridbag.setConstraints(zoneNumber, c);
        add(zoneNumber);

        zoneLetter = new JTextField(2);
        zoneLetter.setToolTipText(i18n.get(UTMCoordPanel.class,
                "hemi",
                I18n.TOOLTIP,
                "Hemisphere: N or S"));
        gridbag.setConstraints(zoneLetter, c);
        add(zoneLetter);

        easting = new JTextField(8);
        gridbag.setConstraints(easting, c);
        add(easting);

        northing = new JTextField(8);
        gridbag.setConstraints(northing, c);
        add(northing);
    }

    /**
     * @return the LatLonPoint represented by contents of the entry
     *         boxes
     */
    public LatLonPoint getLatLon() {
        int iZoneNumber;
        char cZoneLetter;

        try {
            // Allow blank minutes and seconds fields to represent
            // zero
            iZoneNumber = Float.valueOf(zoneNumber.getText()).intValue();
            cZoneLetter = zoneLetter.getText().charAt(0);

            float minEasting = easting.getText().equals("") ? 0f
                    : Float.valueOf(easting.getText()).floatValue();
            easting.setText(Float.toString(Math.abs(minEasting)));

            float minNorthing = northing.getText().equals("") ? 0
                    : Float.valueOf(northing.getText()).floatValue();
            northing.setText(Float.toString(Math.abs(minNorthing)));

            UTMPoint utm = new UTMPoint(minNorthing, minEasting, iZoneNumber, cZoneLetter);
            return utm.toLatLonPoint();

        } catch (NumberFormatException except) {
            Debug.output(except.toString());
            clearTextBoxes();
        }
        return null;
    }

    /**
     * Sets the contents of the latitude and longitude entry boxes
     * 
     * @param llpoint the object containing the coordinates that
     *        should go in the boxes.
     */
    public void setLatLon(LatLonPoint llpoint) {
        if (llpoint == null) {
            clearTextBoxes();
            return;
        }

        UTMPoint utm = new UTMPoint(llpoint);
        northing.setText(Float.toString(utm.northing));
        easting.setText(Float.toString(utm.easting));
        zoneNumber.setText(Integer.toString(utm.zone_number));
        zoneLetter.setText((char) utm.zone_letter + "");
    }

    protected void clearTextBoxes() {
        northing.setText("");
        easting.setText("");
        zoneLetter.setText("");
        zoneNumber.setText("");
    }
}