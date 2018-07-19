/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2000-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    NOTE: permission has been given to the JScience project (http://www.jscience.org)
 *          to distribute this file under BSD-like license.
 */
package edu.cpp.cs499.tanTime;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.PrintStream;


/**
 * Approximations de quelques calculs astronomiques relatifs aux calendriers terrestres.
 * Les diff�rents cycles astronomiques (notamment le jour, le mois et l'ann�e) ne sont pas
 * constants. Par exemple, la longueur de l'ann�e tropicale (le nombre moyen de jours entre
 * deux �quinoxes vernales) �tait d'environ 365,242196 jours en 1900 et devrait �tre d'environ
 * 365,242184 jours en 2100, soit un changement d'environ 1 seconde. Cette classe permet de
 * calculer la longueur d'une ann�e ou d'un mois � une date sp�cifi�e. Toutefois, il est
 * important de noter que les intervalles de temps calcul�s par les m�thodes de cette classe
 * sont des <strong>moyennes</strong>. Pour une ann�e en particulier, l'intervalle de temps
 * d'un �quinoxe vernale au prochain peut s'�carter de cette moyenne de plusieurs minutes.
 * <p>
 * Les calculs de la longueur de l'ann�e tropicale sont bas�s sur les travaux de Laskar (1986).
 * Les calculs de la longueur des mois synodiques sont bas�s sur les travaux de Chapront-Touze et
 * Chapront (1988).On peut lire plus de d�tails au sujet des calendrier terrestre au site
 * <a href="http://webexhibits.org/calendars/year-astronomy.html">http://webexhibits.org/calendars/year-astronomy.html</a>
 * ainsi que
 * <a href="http://www.treasure-troves.com/astro/TropicalYear.html">http://www.treasure-troves.com/astro/TropicalYear.html</a>.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.0
 * @module
 */
public final class Calendar {
    /**
     * Nombre de millisecondes dans une journ�e. Cette constante est
     * utilis�e pour convertir des intervalles de temps du Java en
     * nombre de jours.
     */
    private static final double MILLIS_IN_DAY = 1000*60*60*24;

    /**
     * Jour julien correspondant � l'�poch du Java (1er janvier 1970 � minuit).
     * Cette constante est utilis�e pour convertir des dates du Java en jour
     * julien.
     *
     * La valeur {@link #julianDay}   du 1er janvier 2000 00:00 GMT est 2451544.5 jours.
     * La valeur {@link Date#getTime} du 1er janvier 2000 00:00 GMT est 10957 jours.
     */
    private static final double JULIAN_DAY_1970 = 2451544.5-10957;

    /**
     * Do not allow instantiation of this class.
     */
    private Calendar() {
    }

    /**
     * Returns the julian day of the given date. This is the number of days elapsed since
     * January 1st, 4713 before J.C. at noon GMT. This is named after <cite>Julius Scaliger</cite>,
     * not to be confused to the number of days elapsed since the beginning of the year (named after
     * <cite>Julius Caesar</cite>).
     *
     * @param  time The time for which to evaluate the julian day.
     * @return Number of days elapsed since January 1st, 4713 before J.C. at noon GMT.
     */
    public static double julianDay(final Date time) {
        return julianDay(time.getTime());
    }

    /**
     * Computes the {@linkplain #julianDay(Date) julian day}.
     *
     * @param time The date in milliseconds elapsed since January 1st, 1970.
     */
    static double julianDay(final long time) {
        return (time/MILLIS_IN_DAY) + JULIAN_DAY_1970;
    }

    /**
     * Retourne le nombre de si�cles �coul�s depuis le 1 janvier 2000 � midi.
     * Cette information est utilis�e dans les formules de Laskar (1986) pour
     * calculer la longueur d'une ann�e tropicale, ainsi que par Chapront-Touze
     * et Chapront (1988) pour la longueur d'un mois synodique.
     */
    static double julianCentury(final Date time) {
        return ((time.getTime() / MILLIS_IN_DAY) + (JULIAN_DAY_1970 - 2451545.0)) / 36525;
    }

    /**
     * Retourne la longueur de l'ann�e tropicale. L'ann�e tropicale est d�finie comme l'intervalle
     * moyen entre deux �quinoxes vernales (autour du 21 mars dans l'h�misph�re nord). Il correspond
     * au cycle des saisons. Cet intervalle de temps est une <strong>moyenne</strong>. Un cycle r�el
     * peut s'�carter de plusieurs minutes de cette moyenne. Notez aussi qu'une ann�e tropicale
     * n'est pas identique � une ann�e sid�rale, qui est le temps requis par la Terre pour compl�ter
     * un orbite autour du Soleil. En l'an 2000, l'ann�e tropicale avait une longueur d'environ
     * 365,2422 jours tandis que l'ann�e sid�rale avait une longueur de 365,2564 jours.
     *
     * @param  time A date in the year for which to compute the length.
     * @return The tropical length of the given year.
     */
    public static double tropicalYearLength(final Date time) {
        final double T = julianCentury(time);
        return 365.2421896698 + T*(-0.00000615359 + T*(-7.29E-10 + T*(2.64E-10)));
    }

    /**
     * Retourne la longueur du mois synodique. Le mois synodique est l'intervalle de temps moyen
     * entre deux conjonctions de la lune et du soleil. Il correspond au cycle des phases de la
     * lune. Cet intervalle de temps est une <strong>moyenne</strong>. Un cycle r�el peut s'�carter
     * de plusieurs heures de cette moyenne.
     *
     * @param  time A date in the month for which to compute the length.
     * @return The synodic length of the given month.
     */
    public static double synodicMonthLength(final Date time) {
        final double T=julianCentury(time);
        return 29.5305888531 + T*(0.00000021621 + T*(-3.64E-10));
    }

    /**
     * Returns a date format to be used by the command line tools.
     */
    static DateFormat getDateFormat() {
        final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format;
    }

    /**
     * Prints the length of a tropical year and a synodic month for the given date.
     * Cette application peut �tre lanc�e avec la syntaxe suivante:
     * This application can be launch from the command line as below:
     *
     * {@preformat shell
     *     java org.geotoolkit.nature.Calendar [date]
     * }
     *
     * where <var>date</var> is an optional argument specifying the date and time in the
     * {@code "yyyy-MM-dd HH:mm:ss"} format, UTC time zone. If this argument is omitted,
     * then the current time is used.
     *
     * @param args The command line argument.
     * @throws ParseException If the date is not properly formatted.
     */
    public static void main(final String[] args) throws ParseException {
        final DateFormat format = getDateFormat();
        final Date time = (args.length != 0) ? format.parse(args[0]) : new Date();
        final PrintStream out = System.out;
        out.print("Date (UTC)   : "); out.println(format.format(time));
        out.print("Tropical year: "); out.println(tropicalYearLength(time));
        out.print("Synodic month: "); out.println(synodicMonthLength(time));
    }
}