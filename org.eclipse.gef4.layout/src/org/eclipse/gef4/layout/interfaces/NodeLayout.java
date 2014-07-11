/*******************************************************************************
 * Copyright (c) 2009-2010 Mateusz Matela and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Mateusz Matela - initial API and implementation
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.gef4.layout.interfaces;

public interface NodeLayout extends EntityLayout {

	/**
	 * @return a subgraph this node belongs to or null if this node is not
	 *         pruned
	 */
	public SubgraphLayout getSubgraph();

	/**
	 * Assigns this NodeLayout to the given SubgraphLayout. Sets the pruned
	 * property of this NodeLayout.
	 * 
	 * @param subgraph
	 *            a subgraph this node should belong to or null if this node
	 *            should not be pruned
	 */
	public void prune(SubgraphLayout subgraph);

	/**
	 * Returns all nodes that are direct successors of this node. Nodes
	 * connected with this node by a bidirectional connection are considered
	 * both successors and predecessors. Any subsequent changes to the returned
	 * array do not affect this node.
	 * 
	 * @return array of successors of this node
	 */
	public NodeLayout[] getSuccessingNodes();

	/**
	 * Returns all nodes that are direct predecessors of this node. Nodes
	 * connected with this node by a bidirectional connection are considered
	 * both successors and predecessors. Any subsequent changes to the returned
	 * array do not affect this node.
	 * 
	 * @return array of predecessors of this node
	 */
	public NodeLayout[] getPredecessingNodes();

	/**
	 * Returns all connections that have this node as a target. All connections
	 * that are bidirectional and are adjacent to this node will be also
	 * included in the result. Any subsequent changes to the returned array do
	 * not affect this node.
	 * 
	 * @return array of connections entering this node
	 */
	public ConnectionLayout[] getIncomingConnections();

	/**
	 * Returns all connections that have this node as a source. All connections
	 * that are bidirectional and are adjacent to this node will be also
	 * included in the result. Any subsequent changes to the returned array do
	 * not affect this node.
	 * 
	 * @return array of connections leaving this node
	 */
	public ConnectionLayout[] getOutgoingConnections();

}
