package ru.job4j.tourist.data_base;

public class DbSchema {
    static final class LocTab {
        static final String NAME = "coordinates_table";
        static final class Cols {
            static final String LATITUDE = "latitude";
            static final String LONGITUDE = "longitude";
            static final String TITLE = "place_title";
        }
    }
    static final class TrackTab {
        static final String NAME = "tracks_table";
        static final class Cols {
        static final String DATE = "journey_date";
        static final String T_TITLE = "journey_title";
        }
    }
    static final class TrackPoints {
        static final String NAME = "track_points_table";
        static final class Cols {
            static final String T_LATITUDE = "point_latitude";
            static final String T_LONGITUDE = "point_longitude";
            static final String FOREIGN_KEY = "foreign_key";
        }
    }
}
