/*
 * Copyright (c) 2001-2003 Regents of the University of California.
 * All rights reserved.
 *
 * See the file LICENSE included in this distribution for details.
 */

package org.peerfact.impl.util.vivaldi;

import java.util.Arrays;

import org.peerfact.impl.simengine.Simulator;


/**
 * Vivaldi (Vivaldi: "A Decentralized Network Coordinate System" - Frank Dabek,
 * Russ Cox, Frans Kaashoek, Robert Morris) calculates virtual network
 * coordinates to hosts such that the distance between the coordinates of two
 * hosts accurately predicts the Internet propagation delay between them. The
 * underlying computation algorithm was initially implemented for Bamboo
 * Distributed Hash Table by Steven E. Czerwinski, and has now be adapted for
 * the purpose of simulations in PeerfactSim. This implementation uses a
 * five-dimensional euclidean space.
 * 
 * 
 * @author Steven E. Czerwinski, Sebastian Kaune
 */

public class VivaldiCoordinate {

	/**
	 * The dampening factor applied to each sample's update. (From Vivaldi's
	 * algorithm.)
	 */
	private double delta = 1d;

	private double[] coordinate;

	/**
	 * Creates an 5-dimensional virtual coordinate at the origin of the space.
	 */
	public VivaldiCoordinate() {
		coordinate = new double[5];
		for (int i = 0; i < coordinate.length; i++) {
			coordinate[i] = 0.0;
		}
	}

	/**
	 * Calculates the distance between this coordinate to the given destination.
	 * This will be an estimate of the one-way network latencies between the two
	 * nodes in milliseconds.
	 * 
	 * @param destination
	 *            The coordinate to compare this object against.
	 * @return The distance between the coordinates, as expressed by one-way
	 *         network latency in milliseconds.
	 */
	public double distance(VivaldiCoordinate destination) {

		double result = 0.0;

		for (int i = 0; i < coordinate.length; i++) {
			result += (coordinate[i] - destination.coordinate[i])
					* (coordinate[i] - destination.coordinate[i]);
		}

		result = Math.sqrt(result);

		return result;
	}

	/**
	 * Returns a unit vector in the direction towards the remote coordinate. If
	 * the coordinates are the same, null is returned since you cannot produce a
	 * unit vector.
	 * 
	 * @param remote
	 *            The remote coordinate.
	 * @return A unit vector in the direction towards the remote, or null if the
	 *         coordinates are the same.
	 */
	private double[] displacement(VivaldiCoordinate remote) {

		double length = distance(remote);

		if (length == 0) {
			return null;
		}

		double[] dir = new double[coordinate.length];

		for (int i = 0; i < coordinate.length; i++) {
			dir[i] = (remote.coordinate[i] - coordinate[i]) / length;
		}

		return dir;
	}

	/**
	 * Creates a unit vector in a random direction.
	 * 
	 */
	private static double[] random_displacement(double[] dir, int size) {
		double tempDir[] = dir;
		if (tempDir == null) {
			tempDir = new double[size];
		}

		double length = 0.0;

		for (int i = 0; i < size; i++) {
			tempDir[i] = Simulator.getRandom().nextDouble() - 0.5;
			length += tempDir[i] * tempDir[i];
		}

		length = Math.sqrt(length);

		// make it a unit vector
		for (int i = 0; i < size; i++) {
			tempDir[i] = tempDir[i] / length;
		}

		return tempDir;
	}

	private static double[] random_displacement(int size) {
		return random_displacement(null, size);
	}

	private double[] random_displacement() {
		return random_displacement(coordinate.length);
	}

	/**
	 * Update this coordinate's position with the given latency sample.
	 * 
	 * @param remoteCoordinate
	 *            The node's coordinate used to get the latency sample.
	 * @param latency
	 *            The measured network latency (one-way, in milliseconds) to the
	 *            remote node.
	 */
	public void update(VivaldiCoordinate remoteCoordinate, double latency) {

		double[] dir = displacement(remoteCoordinate);

		if (dir == null) {
			dir = random_displacement(); /* a unit vector in random direction */
		}

		// distance from spring's rest position
		double d = remoteCoordinate.distance(this) - latency;

		// update delta, the dampening factor
		delta -= .025;
		if (delta < .05) {
			delta = .05;
		}

		// displacement from rest position, with dampening factor, and apply
		for (int i = 0; i < coordinate.length; i++) {
			coordinate[i] += dir[i] * d * delta;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(coordinate);
		long temp;
		temp = Double.doubleToLongBits(delta);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof VivaldiCoordinate)) {
			return false;
		}
		VivaldiCoordinate other = (VivaldiCoordinate) obj;
		if (!Arrays.equals(coordinate, other.coordinate)) {
			return false;
		}
		if (Double.doubleToLongBits(delta) != Double
				.doubleToLongBits(other.delta)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(");
		for (int i = 0; i < coordinate.length - 1; i++) {
			buf.append((coordinate[i] + ","));
		}
		buf.append(coordinate[coordinate.length - 1] + ")");
		return buf.toString();
	}

}
