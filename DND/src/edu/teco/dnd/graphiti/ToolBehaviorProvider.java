package edu.teco.dnd.graphiti;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.ConnectionCreationToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonPadData;

import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.graphiti.model.InputModel;
import edu.teco.dnd.graphiti.model.OutputModel;

/**
 * Provides the palette (available {@link FunctionBlockModel}s), tool tips and context buttons.
 */
public class ToolBehaviorProvider extends DefaultToolBehaviorProvider {
	public ToolBehaviorProvider(final DiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}

	@Override
	public IPaletteCompartmentEntry[] getPalette() {
		List<IPaletteCompartmentEntry> palette = new ArrayList<IPaletteCompartmentEntry>();
		PaletteCompartmentEntry connections = new PaletteCompartmentEntry(Messages.Graphiti_PALETTE_CONNECTIONS, null);
		palette.add(connections);
		CreateDataConnectionFeature dataConnectionFeature =
				new CreateDataConnectionFeature((FeatureProvider) getFeatureProvider());
		ConnectionCreationToolEntry connectionCreationToolEntry =
				new ConnectionCreationToolEntry(dataConnectionFeature.getName(),
						dataConnectionFeature.getDescription(), null, null);
		connections.addToolEntry(connectionCreationToolEntry);
		connectionCreationToolEntry.addCreateConnectionFeature(dataConnectionFeature);

		Map<String, List<ICreateFeature>> categories = new HashMap<String, List<ICreateFeature>>();
		for (ICreateFeature cf : getFeatureProvider().getCreateFeatures()) {
			String category = Messages.Graphiti_PALETTE_OTHER;
			if (cf instanceof CreateBlockFeature) {
				category = Messages.Graphiti_PALETTE_BLOCKS;
				String[] classname = ((CreateBlockFeature) cf).getBlockClass().getClassName().split("\\.");
				if (classname.length >= 2) {
					category = classname[classname.length - 2];
				}
			}
			if (!categories.containsKey(category)) {
				categories.put(category, new ArrayList<ICreateFeature>());
			}
			categories.get(category).add(cf);
		}
		List<String> categoryList = new ArrayList<String>(categories.keySet());
		Collections.sort(categoryList);

		for (String category : categoryList) {
			PaletteCompartmentEntry pce = new PaletteCompartmentEntry(category, null);
			List<ICreateFeature> cfs = categories.get(category);
			Collections.sort(cfs, new Comparator<ICreateFeature>() {
				@Override
				public int compare(final ICreateFeature o1, final ICreateFeature o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			for (ICreateFeature cf : cfs) {
				pce.addToolEntry(new ObjectCreationToolEntry(cf.getName(), cf.getDescription(), null, null, cf));
			}
			palette.add(pce);
		}
		return palette.toArray(new IPaletteCompartmentEntry[0]);
	}

	@Override
	public String getToolTip(final GraphicsAlgorithm ga) {
		PictogramElement pe = ga.getPictogramElement();
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		String name = null;
		if (bo instanceof FunctionBlockModel) {
			if (ga instanceof Text) {
				if (TypePropertyUtil.isBlockNameText(ga)) {
					name = ((FunctionBlockModel) bo).getBlockName();
				} else if (TypePropertyUtil.isPositionText(ga)) {
					name = ((FunctionBlockModel) bo).getPosition();
				} else {
					name = ((Text) ga).getValue();
				}
			} else {
				name = ((FunctionBlockModel) bo).getTypeName();
			}
		} else if (bo instanceof OutputModel) {
			OutputModel output = (OutputModel) bo;
			name =
					output.getName() + Messages.Graphiti_SPACE + Messages.Graphiti_BRACE_LEFT
							+ getClassName(output.getType()) + Messages.Graphiti_BRACE_RIGHT; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (bo instanceof InputModel) {
			InputModel input = (InputModel) bo;
			name =
					input.getName() + Messages.Graphiti_SPACE + Messages.Graphiti_BRACE_LEFT
							+ getClassName(input.getType()) + Messages.Graphiti_BRACE_RIGHT; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else if (ga instanceof Text) {
			name = ((Text) ga).getValue();
		}
		if (name != null && !name.isEmpty()) {
			return name;
		}
		final Object superTip = super.getToolTip(ga);
		return superTip instanceof String ? (String) superTip : Messages.Graphiti_EMPTYSTRING; //$NON-NLS-1$
	}

	/**
	 * Returns the class name part of a name that includes the package.
	 * 
	 * @param fullName
	 *            name with package.
	 * @return class name without the package prefix
	 */
	private static String getClassName(final String fullName) {
		if (fullName == null) {
			return null;
		}
		return fullName.substring(fullName.lastIndexOf('.') + 1);
	}

	@Override
	public IContextButtonPadData getContextButtonPad(IPictogramElementContext context) {
		IContextButtonPadData data = super.getContextButtonPad(context);
		PictogramElement pe = context.getPictogramElement();
		setGenericContextButtons(data, pe, CONTEXT_BUTTON_DELETE | CONTEXT_BUTTON_UPDATE);
		return data;
	}
}
