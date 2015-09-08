/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnHoverPolicy;
import org.eclipse.gef4.mvc.models.HoverModel;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.SynchronizeContentAnchoragesOperation;
import org.eclipse.gef4.mvc.operations.SynchronizeContentChildrenOperation;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The {@link GraphContentPart} is the controller for a {@link Graph} content
 * object. It fires two special property changes:
 * <ul>
 * <li>{@link #ACTIVATION_COMPLETE_PROPERTY}
 * <li>{@link #SYNC_COMPLETE_PROPERTY}
 * </ul>
 *
 * @author mwienand
 *
 */
public class GraphContentPart extends AbstractFXContentPart<Group> {

	/**
	 * A property change event is fired as soon as {@link #activate()
	 * activation} has finished.
	 */
	public static final String ACTIVATION_COMPLETE_PROPERTY = "activationComplete";

	/**
	 * A property change event for this property name is fired when a content
	 * synchronization, based on a Graph property change, has finished.
	 */
	public static final String SYNC_COMPLETE_PROPERTY = "synchronizationComplete";

	private PropertyChangeListener graphPropertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (Graph.ATTRIBUTES_PROPERTY.equals(evt.getPropertyName())) {
				// the layout algorithm might have changed
				refreshVisual();
			} else if (Graph.NODES_PROPERTY.equals(evt.getPropertyName())
					|| Graph.EDGES_PROPERTY.equals(evt.getPropertyName())) {
				// construct new layout context
				getAdapter(GraphLayoutContext.class).setGraph(getContent());
				// start content synchronization
				SynchronizeContentChildrenOperation<Node> syncChildrenOp = new SynchronizeContentChildrenOperation<Node>(
						"SynchronizeContentChildren", GraphContentPart.this);
				SynchronizeContentAnchoragesOperation<Node> syncAnchoragesOp = new SynchronizeContentAnchoragesOperation<Node>(
						"SynchronizeContentAnchorage", GraphContentPart.this);
				ForwardUndoCompositeOperation syncOp = new ForwardUndoCompositeOperation("SynchronizeContent");
				syncOp.add(syncChildrenOp);
				syncOp.add(syncAnchoragesOp);
				try {
					syncOp.execute(null, null);
					pcs.firePropertyChange(SYNC_COMPLETE_PROPERTY, false, true);
				} catch (ExecutionException e) {
					throw new IllegalStateException("Cannot synchronize with contents.", e);
				}
			}
		}
	};

	/**
	 * Constructs a new {@link GraphContentPart}. Installs a "NoHoverPolicy" for
	 * the {@link GraphContentPart}, so that it will not be put into the
	 * {@link HoverModel} when hovered.
	 */
	public GraphContentPart() {
		// we set the hover policy adapter here to disable hovering this part
		// TODO: move to NoHoverPolicy
		setAdapter(AdapterKey.get(AbstractFXOnHoverPolicy.class), new AbstractFXOnHoverPolicy() {
			@Override
			public void hover(MouseEvent e) {
			}
		});
	}

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		getVisual().getChildren().add(index, child.getVisual());
	}

	@Override
	protected Group createVisual() {
		Group visual = new Group();
		visual.setAutoSizeChildren(false);
		return visual;
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		getContent().addPropertyChangeListener(graphPropertyChangeListener);
		pcs.firePropertyChange(ACTIVATION_COMPLETE_PROPERTY, false, true);
		refreshVisual();
	}

	@Override
	protected void doDeactivate() {
		getContent().removePropertyChangeListener(graphPropertyChangeListener);
		super.doDeactivate();
	}

	@Override
	public void doRefreshVisual(Group visual) {
		// set layout algorithm from Graph on the context
		setGraphLayoutAlgorithm();
		// TODO: setGraphStyleSheet();
	}

	@Override
	public Graph getContent() {
		return (Graph) super.getContent();
	}

	@Override
	public List<Object> getContentChildren() {
		List<Object> children = new ArrayList<Object>();
		children.addAll(getContent().getEdges());
		children.addAll(getContent().getNodes());
		return children;
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		getVisual().getChildren().remove(child.getVisual());
	}

	@Override
	public void setContent(Object content) {
		super.setContent(content);
		getAdapter(GraphLayoutContext.class).setGraph(getContent());
	}

	private void setGraphLayoutAlgorithm() {
		Object algo = getContent().getAttrs().get(ZestProperties.GRAPH_LAYOUT);
		if (algo instanceof ILayoutAlgorithm) {
			ILayoutAlgorithm layoutAlgorithm = (ILayoutAlgorithm) algo;
			ILayoutContext layoutContext = getAdapter(GraphLayoutContext.class);
			if (layoutContext != null && layoutContext.getStaticLayoutAlgorithm() != algo) {
				layoutContext.setStaticLayoutAlgorithm(layoutAlgorithm);
			}
		}
	}

}
