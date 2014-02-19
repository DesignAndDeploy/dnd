package edu.teco.dnd.graphiti;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;

import edu.teco.dnd.graphiti.model.FunctionBlockModel;
import edu.teco.dnd.graphiti.model.InputModel;
import edu.teco.dnd.graphiti.model.OptionModel;
import edu.teco.dnd.graphiti.model.OutputModel;

/**
 * Adds the graphical representation of a {@link FunctionBlockModel}.
 * 
 * @see CreateBlockFeature
 */
public class AddBlockFeature extends AbstractAddShapeFeature {
	private static final Logger LOGGER = LogManager.getLogger(AddBlockFeature.class);

	public static final String TEXT_KEY = "text-id"; //$NON-NLS-1$
	public static final String CONNECTION_KEY = "connection"; //$NON-NLS-1$
	public static final String CONNECTION_VALUE = CONNECTION_KEY;

	public static final int DEFAULT_WIDTH = 100;
	public static final int DEFAULT_HEIGHT = 50;
	public static final int CORNER_RADIUS = 5;
	public static final int SEPARATOR_Y = 20;
	public static final int BLOCKNAME_OFFSET = 35;
	public static final int BLOCKNAME_SIZE = 15;
	public static final int POSITION_OFFSET = 65;
	public static final int POSITION_SIZE = 15;
	public static final int CONNECTION_OFFSET = 105;
	public static final int CONNECTION_SIZE = 15;
	public static final int CONNECTION_SPACE = 7;
	public static final int CONNECTION_EXTRA = 2;
	public static final int OPTION_EXTRA = 7;

	public static final IColorConstant FOREGROUND = IColorConstant.BLACK;
	public static final IColorConstant BACKGROUND_SENSOR = new ColorConstant("238BC2"); //$NON-NLS-1$
	public static final IColorConstant BACKGROUND_ACTOR = new ColorConstant("BC80FF"); //$NON-NLS-1$
	public static final IColorConstant BACKGROUND_OTHER = new ColorConstant("AAAAAA"); //$NON-NLS-1$
	public static final IColorConstant TEXT = IColorConstant.BLACK;
	public static final IColorConstant INPUT = new ColorConstant("FFFF00"); //$NON-NLS-1$
	public static final IColorConstant OUTPUT = new ColorConstant("41DB00"); //$NON-NLS-1$

	public static final Integer FONT_SIZE = 8;

	public AddBlockFeature(final IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public final boolean canAdd(final IAddContext context) {
		LOGGER.entry(context);
		boolean ret =
				context.getNewObject() instanceof FunctionBlockModel && context.getTargetContainer() instanceof Diagram;
		LOGGER.exit(ret);
		return ret;
	}

	@Override
	public final PictogramElement add(final IAddContext context) {
		LOGGER.entry(context);
		FunctionBlockModel addedBlock = (FunctionBlockModel) context.getNewObject();
		Diagram targetDiagram = (Diagram) context.getTargetContainer();
		LOGGER.debug("Adding {} to {}", addedBlock, targetDiagram); //$NON-NLS-1$

		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

		IGaService gaService = Graphiti.getGaService();
		RoundedRectangle roundedRectangle;

		{
			LOGGER.debug("creating outer rectangle"); //$NON-NLS-1$
			roundedRectangle = gaService.createRoundedRectangle(containerShape, CORNER_RADIUS, CORNER_RADIUS);
			if (addedBlock.isSensor()) {
				roundedRectangle.setBackground(manageColor(BACKGROUND_SENSOR));
			} else if (addedBlock.isActor()) {
				roundedRectangle.setBackground(manageColor(BACKGROUND_ACTOR));
			} else {
				roundedRectangle.setBackground(manageColor(BACKGROUND_OTHER));
			}
			roundedRectangle.setForeground(manageColor(FOREGROUND));
			roundedRectangle.setLineWidth(2);
			gaService.setLocationAndSize(roundedRectangle, context.getX(), context.getY(), DEFAULT_WIDTH,
					DEFAULT_HEIGHT);

			if (addedBlock.eResource() == null) {
				getDiagram().eResource().getContents().add(addedBlock);
			}

			TypePropertyUtil.setBlockShape(roundedRectangle);

			link(containerShape, addedBlock);
		}

		{
			LOGGER.debug("adding separator"); //$NON-NLS-1$
			Shape shape = peCreateService.createShape(containerShape, false);

			Polyline polyline =
					gaService.createPolyline(shape, new int[] { 0, SEPARATOR_Y, DEFAULT_WIDTH, SEPARATOR_Y });
			polyline.setForeground(manageColor(FOREGROUND));
			polyline.setLineWidth(2);
		}

		{
			LOGGER.debug("adding name"); //$NON-NLS-1$
			Shape shape = peCreateService.createShape(containerShape, false);

			Text text = gaService.createText(shape, addedBlock.getTypeName());
			text.setForeground(manageColor(TEXT));
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setFont(gaService
					.manageFont(getDiagram(), Messages.Graphiti_addBlock_FONTNAME, FONT_SIZE, false, true));
			gaService.setLocationAndSize(text, 0, 0, DEFAULT_WIDTH, SEPARATOR_Y);

			link(shape, addedBlock);
		}

		{
			LOGGER.debug("adding blockName field"); //$NON-NLS-1$
			Shape nameShape = peCreateService.createShape(containerShape, false);
			Text nameText = gaService.createText(nameShape, Messages.Graphiti_addBlock_NAME);
			nameText.setForeground(manageColor(TEXT));
			nameText.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
			gaService.setLocationAndSize(nameText, OPTION_EXTRA, BLOCKNAME_OFFSET - BLOCKNAME_SIZE / 2, DEFAULT_WIDTH
					/ 2 - 2 * OPTION_EXTRA, BLOCKNAME_SIZE);

			Shape valueShape = peCreateService.createShape(containerShape, false);
			String blockName = addedBlock.getBlockName();
			if (blockName == null) {
				blockName = Messages.Graphiti_EMPTYSTRING;
			}
			Text valueText = gaService.createText(valueShape, blockName);
			valueText.setForeground(manageColor(TEXT));
			valueText.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
			gaService.setLocationAndSize(valueText, DEFAULT_WIDTH / 2 + OPTION_EXTRA, BLOCKNAME_OFFSET - BLOCKNAME_SIZE
					/ 2, DEFAULT_WIDTH / 2 - 2 * OPTION_EXTRA, BLOCKNAME_SIZE);
			TypePropertyUtil.setBlockNameText(valueText);
			link(valueShape, addedBlock);
		}

		{
			LOGGER.debug("adding position"); //$NON-NLS-1$
			Shape nameShape = peCreateService.createShape(containerShape, false);
			Text nameText = gaService.createText(nameShape, Messages.Graphiti_addBlock_POSITION);
			nameText.setForeground(manageColor(TEXT));
			nameText.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
			gaService.setLocationAndSize(nameText, OPTION_EXTRA, POSITION_OFFSET - POSITION_SIZE / 2, DEFAULT_WIDTH / 2
					- 2 * OPTION_EXTRA, POSITION_SIZE);

			Shape valueShape = peCreateService.createShape(containerShape, false);
			String position = addedBlock.getPosition();
			if (position == null) {
				position = Messages.Graphiti_EMPTYSTRING; //$NON-NLS-1$
			}
			Text valueText = gaService.createText(valueShape, position);
			valueText.setForeground(manageColor(TEXT));
			valueText.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
			gaService.setLocationAndSize(valueText, DEFAULT_WIDTH / 2 + OPTION_EXTRA, POSITION_OFFSET - POSITION_SIZE
					/ 2, DEFAULT_WIDTH / 2 - 2 * OPTION_EXTRA, POSITION_SIZE);
			TypePropertyUtil.setPositionText(valueText);
			link(valueShape, addedBlock);
		}

		createConnectionsAndOptions(addedBlock, containerShape, peCreateService, gaService);

		LOGGER.debug("calling layout on {}", containerShape); //$NON-NLS-1$
		layoutPictogramElement(containerShape);

		LOGGER.exit(containerShape);

		return containerShape;
	}

	/**
	 * Creates graphical representations for Inputs, Outputs and Options.
	 * 
	 * @param addedBlock
	 *            the block that was added
	 * @param peCreateService
	 *            create service for PictogramElements
	 * @param containerShape
	 *            the container shape for the block
	 * @param gaService
	 *            the GraphicsAlgorithmService
	 */
	private void createConnectionsAndOptions(final FunctionBlockModel addedBlock, final ContainerShape containerShape,
			final IPeCreateService peCreateService, final IGaService gaService) {
		{
			LOGGER.debug("adding inputs"); //$NON-NLS-1$
			int pos = CONNECTION_OFFSET;
			for (Object inputObject : addedBlock.getInputs()) {
				final InputModel input = (InputModel) inputObject;
				LOGGER.trace("adding {} at {}", input, pos); //$NON-NLS-1$
				FixPointAnchor anchor = peCreateService.createFixPointAnchor(containerShape);
				anchor.setLocation(gaService.createPoint(CONNECTION_SIZE / 2 + CONNECTION_EXTRA, pos - CONNECTION_SIZE
						/ 2));
				Ellipse ellipse = gaService.createEllipse(anchor);
				ellipse.setForeground(manageColor(FOREGROUND));
				ellipse.setBackground(manageColor(INPUT));
				ellipse.setLineWidth(2);
				gaService.setLocationAndSize(ellipse, -CONNECTION_SIZE / 2 + CONNECTION_EXTRA, -CONNECTION_SIZE / 2,
						CONNECTION_SIZE, CONNECTION_SIZE);

				Shape labelShape = peCreateService.createShape(containerShape, false);
				Text label = gaService.createText(labelShape, input.getName());
				label.setForeground(manageColor(TEXT));
				label.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
				gaService.setLocationAndSize(label, CONNECTION_SIZE + 2 * CONNECTION_EXTRA, pos - CONNECTION_SIZE,
						DEFAULT_WIDTH / 2 - CONNECTION_SIZE - CONNECTION_EXTRA * 3, CONNECTION_SIZE);
				Graphiti.getPeService().setPropertyValue(label, CONNECTION_KEY, CONNECTION_VALUE);

				pos += CONNECTION_SIZE + CONNECTION_SPACE;
				link(anchor, input);
			}
		}

		{
			LOGGER.debug("adding outputs"); //$NON-NLS-1$
			int pos = CONNECTION_OFFSET;
			for (Object outputObject : addedBlock.getOutputs()) {
				final OutputModel output = (OutputModel) outputObject;
				LOGGER.trace("adding {} at {}", output, pos); //$NON-NLS-1$
				FixPointAnchor anchor = peCreateService.createFixPointAnchor(containerShape);
				anchor.setLocation(gaService.createPoint(DEFAULT_WIDTH - CONNECTION_SIZE / 2 - CONNECTION_EXTRA, pos
						- CONNECTION_SIZE / 2));
				Ellipse ellipse = gaService.createEllipse(anchor);
				ellipse.setForeground(manageColor(FOREGROUND));
				ellipse.setBackground(manageColor(OUTPUT));
				ellipse.setLineWidth(2);
				gaService.setLocationAndSize(ellipse, -CONNECTION_SIZE / 2 - CONNECTION_EXTRA, -CONNECTION_SIZE / 2,
						CONNECTION_SIZE, CONNECTION_SIZE);

				Shape labelShape = peCreateService.createShape(containerShape, false);
				Text label = gaService.createText(labelShape, output.getName());
				label.setForeground(manageColor(TEXT));
				label.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
				gaService.setLocationAndSize(label, DEFAULT_WIDTH / 2 + CONNECTION_SIZE + 2 * CONNECTION_EXTRA, pos
						- CONNECTION_SIZE, DEFAULT_WIDTH / 2 - CONNECTION_SIZE - CONNECTION_EXTRA * 5, CONNECTION_SIZE);
				Graphiti.getPeService().setPropertyValue(label, CONNECTION_KEY, CONNECTION_VALUE);
				LOGGER.trace("{}", Graphiti.getPeService().getProperty(label, CONNECTION_KEY)); //$NON-NLS-1$

				pos += CONNECTION_SIZE + CONNECTION_SPACE;
				link(anchor, output);
			}
		}

		{
			LOGGER.debug("adding options"); //$NON-NLS-1$
			int pos =
					AddBlockFeature.CONNECTION_OFFSET
							+ Math.max(addedBlock.getInputs().size(), addedBlock.getOutputs().size())
							* (AddBlockFeature.CONNECTION_SIZE + AddBlockFeature.CONNECTION_SPACE);
			for (Object optionObject : addedBlock.getOptions()) {
				final OptionModel option = (OptionModel) optionObject;
				LOGGER.trace("adding {} at {}", option, pos); //$NON-NLS-1$
				Shape nameShape = peCreateService.createShape(containerShape, false);
				Text nameText = gaService.createText(nameShape, option.getName() + ":"); //$NON-NLS-1$
				nameText.setForeground(manageColor(TEXT));
				nameText.setHorizontalAlignment(Orientation.ALIGNMENT_LEFT);
				gaService.setLocationAndSize(nameText, OPTION_EXTRA, pos - CONNECTION_SIZE / 2, DEFAULT_WIDTH / 2 - 2
						* OPTION_EXTRA, CONNECTION_SIZE);

				Shape valueShape = peCreateService.createShape(containerShape, false);
				Text valueText = gaService.createText(valueShape, Messages.Graphiti_EMPTYSTRING + option.getValue()); //$NON-NLS-1$
				valueText.setForeground(manageColor(TEXT));
				valueText.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
				gaService.setLocationAndSize(valueText, DEFAULT_WIDTH / 2 + OPTION_EXTRA, pos - CONNECTION_SIZE / 2,
						DEFAULT_WIDTH / 2 - 2 * OPTION_EXTRA, CONNECTION_SIZE);

				pos += CONNECTION_SIZE + CONNECTION_SPACE;
				link(valueShape, option);
			}
		}
	}
}
