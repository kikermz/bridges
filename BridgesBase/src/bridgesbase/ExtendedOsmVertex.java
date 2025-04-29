package bridgesbase;

import bridges.data_src_dependent.OsmVertex;

public class ExtendedOsmVertex extends OsmVertex {
    public ExtendedOsmVertex(double latitude, double longitude) {
        super(latitude, longitude);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ExtendedOsmVertex other = (ExtendedOsmVertex) obj;
        return Double.compare(getLatitude(), other.getLatitude()) == 0 &&
               Double.compare(getLongitude(), other.getLongitude()) == 0;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getLatitude(), getLongitude());
    }
}