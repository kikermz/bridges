//Geometric utility functions
//Created by James Vanderhyde, 13 February 2025

package edu.sxu.cs.geometry;

public final class Geometry 
{
    public static double squareDistance(Point a, Point b)
    {
        return (b.x-a.x)*(b.x-a.x) + (b.y-a.y)*(b.y-a.y);
    }
    
    public static double distance(Point a, Point b)
    {
        return Math.sqrt(squareDistance(a, b));
    }
    
    public static double absoluteDistance(Point p, Line l)
    {
        return Math.abs(signedDistance(p,l));
    }
    
    public static double signedDistance(Point p, Line l)
    {
        return signedDistanceUnnormalized(p,l)/distance(l.a,l.b);
    }
    
    public static double signedDistanceUnnormalized(Point p, Line l)
    {
        return (l.b.y-l.a.y)*p.x - (l.b.x-l.a.x)*p.y + l.b.x*l.a.y - l.b.y*l.a.x;
    }

}
