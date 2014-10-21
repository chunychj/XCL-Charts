/**
 * Copyright 2014  XCL-Charts
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 	
 * @Project XCL-Charts 
 * @Description Android图表基类库
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version 1.0
 */

package org.xclcharts.chart;

import java.util.ArrayList;
import java.util.List;

import org.xclcharts.common.DrawHelper;
import org.xclcharts.common.MathHelper;
import org.xclcharts.event.click.BarPosition;
import org.xclcharts.renderer.AxisChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.axis.AxisTick;
import org.xclcharts.renderer.bar.Bar;
import org.xclcharts.renderer.bar.FlatBar;
import org.xclcharts.renderer.line.PlotCustomLine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.Log;

/**
 * @ClassName BarChart
 * @Description  柱形图的基类,包含横向和竖向柱形图
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 *  
 */

public class BarChart extends AxisChart {
	
	private static final String TAG = "BarChart";

	// 柱形基类
	private FlatBar mFlatBar = new FlatBar();
	// 数据源
	private List<BarData> mDataSet;
	// 确定是竖向柱形图(默认)还是横向
	private XEnum.Direction mDirection = XEnum.Direction.VERTICAL;

	//用于绘制定制线(分界线)
	private PlotCustomLine mCustomLine = null;	
	
	//轴刻度
	protected ArrayList<AxisTick> mLstDataTick = new  ArrayList<AxisTick>();
	protected ArrayList<AxisTick> mLstCateTick = new  ArrayList<AxisTick>();

	public BarChart() {
						
		//默认为竖向设置
		defaultAxisSetting();
	}
	
	@Override
	public XEnum.ChartType getType()
	{
		return XEnum.ChartType.BAR;
	}

	/**
	 * 开放柱形绘制类
	 * @return 柱形绘制类
	 */
	public Bar getBar() {
		return mFlatBar;
	}
	

	/**
	 * 设置定制线值
	 * @param customLineDataSet 定制线数据集合
	 */
	public void setCustomLines( List<CustomLineData> customLineDataSet)
	{
		if(null == mCustomLine)mCustomLine = new PlotCustomLine();
		mCustomLine.setCustomLines(customLineDataSet);
	}

	/**
	 * 分类轴的数据源
	 * 
	 * @param categories
	 *            分类集
	 */
	public void setCategories( List<String> categories) {	
		if(null != categoryAxis)categoryAxis.setDataBuilding(categories);
	}

	/**
	 * 设置数据轴的数据源
	 * 
	 * @param dataSeries
	 *            数据源
	 */
	public void setDataSource( List<BarData> dataSeries) {		
		this.mDataSet = dataSeries;
	}

	/**
	 * 返回数据库的数据源
	 * @return 数据源
	 */
	public List<BarData> getDataSource() {
		return mDataSet;
	}

	/**
	 * 设置图的显示方向,即横向还是竖向显示柱形
	 * @param direction 横向/竖向
	 */
	public void setChartDirection( XEnum.Direction direction) {
		mDirection = direction;
		
		defaultAxisSetting();		
	}
	
	/**
	 * 返回图的显示方向,即横向还是竖向显示柱形
	 * @return 横向/竖向
	 */
	public XEnum.Direction getChartDirection()
	{
		return mDirection;
	}
	
	/**
	 * 图为横向或竖向时，轴和Bar的默认显示风格
	 */
	private void defaultAxisSetting()
	{								
		categoryAxisDefaultSetting();
		dataAxisDefaultSetting();
				
		if(null != getBar())
		{
				switch (mDirection) {
					case HORIZONTAL: 	
						getBar().getItemLabelPaint().setTextAlign(Align.LEFT);						
						getBar().setBarDirection(XEnum.Direction.HORIZONTAL);							
						break;				
					case VERTICAL: 
						getBar().setBarDirection(XEnum.Direction.VERTICAL);				
						break;				
				}
		}
	}
	
	
	private void categoryAxisDefaultSetting()
	{		
		if(null == categoryAxis) return;
		
		switch (mDirection) {
			case HORIZONTAL:					
					categoryAxis.setHorizontalTickAlign(Align.LEFT);		
					categoryAxis.getTickLabelPaint().setTextAlign(Align.RIGHT);
					categoryAxis.setVerticalTickPosition(XEnum.VerticalAlign.MIDDLE);
				break;			
			 case VERTICAL: 					
					categoryAxis.setHorizontalTickAlign(Align.CENTER);			
					categoryAxis.getTickLabelPaint().setTextAlign(Align.CENTER);					
					categoryAxis.setVerticalTickPosition(XEnum.VerticalAlign.BOTTOM);
				break;		
		}
	}
	

	private void dataAxisDefaultSetting()
	{		
		if(null == dataAxis) return;
			
		switch (mDirection) {
			case HORIZONTAL:					
					dataAxis.setHorizontalTickAlign(Align.CENTER);
					dataAxis.getTickLabelPaint().setTextAlign(Align.CENTER);
					dataAxis.setVerticalTickPosition(XEnum.VerticalAlign.BOTTOM);
				break;
			case VERTICAL: 					
					dataAxis.setHorizontalTickAlign(Align.LEFT);
					dataAxis.getTickLabelPaint().setTextAlign(Align.RIGHT);	
					dataAxis.setVerticalTickPosition(XEnum.VerticalAlign.MIDDLE);
				break;
		}
	}
	
	/**
	 * 比较传入的各个数据集，找出最大数据个数
	 * @return 最大数据个数
	 */
	protected int getDataAxisDetailSetMaxSize() {
		// 得到最大size个数
		int dsetMaxSize = 0;
		int size = mDataSet.size();
		for (int i = 0; i < size; i++) {
			if (dsetMaxSize < mDataSet.get(i).getDataSet().size())
				dsetMaxSize = mDataSet.get(i).getDataSet().size();
		}
		return dsetMaxSize;
	}

	/**
	 * 	竖向柱形图
	 *  Y轴的屏幕高度/数据轴的刻度标记总数 = 步长
	 * @return Y轴步长
	 */
	private float getVerticalYSteps(int tickCount) {		
		return (div(getPlotScreenHeight(),tickCount)); //getAxisScreenHeight
	}

	/**
	 * 竖向柱形图
	 * 得到X轴的步长
	 * X轴的屏幕宽度 / 刻度标记总数  = 步长
	 * @param num 刻度标记总数 
	 * @return X轴步长
	 */
	protected float getVerticalXSteps(int num) {
		//柱形图为了让柱形显示在tick的中间，会多出一个步长即(dataSet.size()+1)			
		return  div(getPlotScreenWidth() ,num); //getAxisScreenWidth
	}

	
	/**
	 * 横向柱形图,Y轴显示分类
	 * Y轴的屏幕高度/(分类轴的刻度标记总数+1) = 步长
	 * @return Y轴步长
	 */
	protected float getHorizontalYSteps() {
		int count = categoryAxis.getDataSet().size() + 1;		
		return div(getPlotScreenHeight() , count );		
	}	
		
	/**
	 * 绘制左边竖轴，及上面的刻度线和分类
	 */
	protected void renderVerticalBarDataAxis(Canvas canvas) {
		// 数据轴数据刻度总个数
		int tickCount = dataAxis.getAixTickCount();
		// 数据轴高度步长
		float YSteps = getVerticalYSteps(tickCount);
		float currentY = 0.0f; 
		
		double slen = 0d;
		double currentTickLabel =  0d;
		
		float axisX =  plotArea.getLeft();
	

		// 数据轴(Y 轴)
		for (int i = 0; i <= tickCount; i++) {
			//if (i == 0)
			//	continue;
			
			
			
			// 依起始数据坐标与数据刻度间距算出上移高度
			if(0 == i)
			{
				currentY = plotArea.getBottom();
				currentTickLabel = dataAxis.getAxisMin();			
				mLstDataTick.add(new AxisTick(i,axisX,currentY, Double.toString(currentTickLabel)));
				continue;
			}else
				currentY = sub(plotArea.getBottom(), mul(i,YSteps));
			
			//是否绘制tick
			if(dataAxis.isShowAxisLabels() &&
					isRenderVerticalBarDataAxisTick(currentY,mMoveY)) continue;
									
			// 分类		
			slen = i * dataAxis.getAxisSteps();			
			currentTickLabel = MathHelper.getInstance().add(dataAxis.getAxisMin(), slen);
					
			// 从左到右的横向网格线		
			if ( i % 2 != 0) 
			{
				plotGrid.renderOddRowsFill(canvas,axisX, add(currentY,YSteps), plotArea.getPlotRight(), currentY);
			} else {
				plotGrid.renderEvenRowsFill(canvas, axisX,add(currentY,YSteps), plotArea.getPlotRight(), currentY);
			}
			
			//将当前为第几个tick传递轴，用以区分是否为主明tick
			dataAxis.setAxisTickCurrentID(i);
			plotGrid.setPrimaryTickLine(dataAxis.isPrimaryTick());
			plotGrid.renderGridLinesHorizontal(canvas, axisX , currentY,plotArea.getPlotRight(), currentY);			
					
			if(i == tickCount)
			{
				mLstDataTick.add(new AxisTick(i,axisX,plotArea.getTop(), Double.toString(currentTickLabel)));
			}else{
				mLstDataTick.add(new AxisTick(i,axisX,currentY, Double.toString(currentTickLabel)));
			}
			
		}
	}

	/**
	 * 绘制竖向柱形图中的底部分类轴
	 */
	protected void renderVerticalBarCategoryAxis(Canvas canvas) {
		
		if(null == categoryAxis)return;
		// 得到分类轴数据集
		List<String> dataSet = categoryAxis.getDataSet();
		if(null == dataSet) return ;
		
		// 分类轴(X 轴)
		float currentX = 0.0f;

		// 依传入的分类个数与轴总宽度算出要画的分类间距数是多少
		// 总宽度 / 分类个数 = 间距长度    //getAxisScreenWidth() 
		float XSteps = div(getPlotScreenWidth() , (dataSet.size() + 1));
		
		for (int i = 0; i < dataSet.size(); i++) {
			// 依初超始X坐标与分类间距算出当前刻度的X坐标
			currentX = add(plotArea.getLeft(),mul((i + 1) , XSteps)); 

			//是否绘制tick
			if(categoryAxis.isShowAxisLabels() && 
					isRenderVerticalCategoryAxisTick(currentX,this.mMoveX))continue;
						
			// 绘制横向网格线
			if (plotGrid.isShowVerticalLines()) {
				canvas.drawLine(currentX, plotArea.getBottom(),
								currentX, plotArea.getTop(),
								plotGrid.getVerticalLinePaint());
			}
			// 画上分类/刻度线
			mLstCateTick.add(new AxisTick(currentX,plotArea.getBottom(), dataSet.get(i)));
		}
	}

	/**
	 * 绘制横向柱形图中的数据轴
	 */
	protected void renderHorizontalBarDataAxis(Canvas canvas) {
		// 依数据轴最大刻度值与数据间的间距 算出要画多少个数据刻度
		int tickCount = dataAxis.getAixTickCount();		
		// 得到数据分类刻度间距  //getAxisScreenWidth
		float XSteps = div(this.getPlotScreenWidth() , tickCount); 

		// x 轴
		float currentX = 0.0f;
		float axisX = plotArea.getLeft();
		double currentTickLabel = 0d;
		
		for(int i = 0; i <= tickCount; i++) {				
			//if (i == 0)continue;
												
			// 依起始数据坐标与数据刻度间距算出上移高度
			if(0 == i)
			{
				currentX = axisX;
				mLstDataTick.add(new AxisTick(currentX,plotArea.getBottom(), 
										Double.toString(dataAxis.getAxisMin())));
				continue;
			}else
				currentX = add(axisX , mul(i , XSteps));
			
			//是否需要绘制tick
			if(dataAxis.isShowAxisLabels() &&
					isRenderHorizontalDataAxisTick(currentX,this.mMoveX))continue;
												
			//标签
			currentTickLabel =  MathHelper.getInstance().add(
										dataAxis.getAxisMin(), i *  dataAxis.getAxisSteps());														
			
			if (i % 2 != 0) {
				plotGrid.renderOddRowsFill(canvas,currentX, plotArea.getTop(),
								sub(currentX , XSteps), plotArea.getBottom());
			} else {
				plotGrid.renderEvenRowsFill(canvas,currentX, plotArea.getTop(),
								sub(currentX , XSteps), plotArea.getBottom());
			}
										
			
			//将当前为第几个tick传递轴，用以区分是否为主明tick
			dataAxis.setAxisTickCurrentID(i);
			// 从底到上的竖向网格线
			plotGrid.setPrimaryTickLine(dataAxis.isPrimaryTick());
						
			plotGrid.renderGridLinesVertical(canvas,currentX,
								plotArea.getBottom(), currentX, plotArea.getTop());
			
			//绘制tick			
			mLstDataTick.add(new AxisTick(i,currentX,plotArea.getBottom(), 
											Double.toString(currentTickLabel)));
			
		}
	}

	/**
	 * 绘制横向柱形图中的分类轴
	 */
	protected void renderHorizontalBarCategoryAxis(Canvas canvas) {
		// Y 轴
		if(null == categoryAxis)return;
		// 分类横向间距高度
		float YSteps = div(getAxisScreenHeight()
								, (categoryAxis.getDataSet().size() + 1));
		
		float currentY = 0.0f;
		float axisX = plotArea.getLeft();
		float axisY = plotArea.getBottom() ;//+ this.mMoveY; // mTranslateXY[1];
		int count = categoryAxis.getDataSet().size();
		for (int i = 0; i < count; i++) {
			// 依初超始Y坐标与分类间距算出当前刻度的Y坐标
			currentY = sub(axisY, mul((i + 1) , YSteps));
			
			//是否绘制tick
			if(categoryAxis.isShowAxisLabels() &&  
					isRenderHorizontalCategoryAxisTick(currentY,this.mMoveY))continue;
			
			// 横的grid线
			plotGrid.renderGridLinesHorizontal(canvas,axisX,
												currentY, plotArea.getPlotRight(), currentY);
			// 分类
			mLstCateTick.add(new AxisTick(axisX,currentY, categoryAxis.getDataSet().get(i)  ));
			
		}
	}		

	/**
	 * 绘制横向柱形图
	 * @throws InterruptedException 
	 */
	protected boolean renderHorizontalBar(Canvas canvas) {
		
		if(null == mDataSet) return false;				

		// 得到Y 轴分类横向间距高度
		float YSteps = getHorizontalYSteps();
		float barInitX = plotArea.getLeft() ;
		float barInitY = plotArea.getBottom() ;
		
		// 画柱形
		// 依柱形宽度，多柱形间的偏移值 与当前数据集的总数据个数得到当前分类柱形要占的高度
		int barNumber = getDatasetSize(mDataSet); 
		int currNumber = 0;
		
		float[] ret = mFlatBar.getBarHeightAndMargin(YSteps, barNumber);
		if(null == ret||ret.length != 2)
		{
			Log.e(TAG,"分隔间距计算失败.");
			return false;
		}
		float barHeight = ret[0];
		float barInnerMargin = ret[1];
		float labelBarUseHeight = add(mul(barNumber , barHeight) ,
									  mul(sub(barNumber , 1) , barInnerMargin));		

		float scrWidth = getPlotScreenWidth(); //getAxisScreenWidth();
		float valueWidth = (float) dataAxis.getAxisRange();
		int barDefualtColor = 0,vSize = 0;
		Double bv = 0d;

		for (int i = 0; i < barNumber; i++) {
			// 得到分类对应的值数据集
			BarData bd = mDataSet.get(i);
			List<Double> barValues = bd.getDataSet();
			if(null == barValues) continue ;
			
			List<Integer> barDataColor = bd.getDataColor();
			// 设置成对应的颜色
			barDefualtColor = bd.getColor();
			mFlatBar.getBarPaint().setColor(barDefualtColor);		
			
			// 画同分类下的所有柱形
			vSize = barValues.size();
			for (int j = 0; j < vSize; j++) {
				bv = barValues.get(j);							
				setBarDataColor(mFlatBar.getBarPaint(),barDataColor,j,barDefualtColor);
											
				float currLableY = sub(barInitY , mul((j + 1) , YSteps));		
				float drawBarButtomY = add(currLableY,labelBarUseHeight / 2);					
				drawBarButtomY = sub(drawBarButtomY, add(barHeight,barInnerMargin) * currNumber);			
				float drawBarTopY = sub(drawBarButtomY,barHeight);			

				// 宽度								
				double vaxlen = MathHelper.getInstance().sub(bv, dataAxis.getAxisMin());				
				float valuePostion = mul(scrWidth, div(dtof(vaxlen) ,valueWidth) );
																			
				// 画出柱形
				float rightX = add(barInitX , valuePostion);
				mFlatBar.renderBar(barInitX,drawBarTopY  ,rightX, drawBarButtomY,
									canvas);
				
				//保存位置
				saveBarRectFRecord(i,j,barInitX + mMoveX,drawBarTopY  + mMoveY,
									rightX  + mMoveX, drawBarButtomY + mMoveY);
			
				// 柱形顶端标识
				mFlatBar.renderBarItemLabel(getFormatterItemLabel(bv),
												rightX, 
												sub(drawBarButtomY , barHeight / 2),canvas);
			}
			currNumber++;
		}

		//画横向柱形图，竖向的定制线
		if(null != mCustomLine)
		{
			mCustomLine.setHorizontalPlot(dataAxis, plotArea, this.getAxisScreenWidth());
			mCustomLine.renderHorizontalCustomlinesDataAxis(canvas);
		}
		return true;
	}		
	
	/**
	 * 绘制竖向柱形图
	 */
	protected boolean renderVerticalBar(Canvas canvas) {
		
		if(null == mDataSet) return false;	
		// 得到分类轴数据集
		List<String> dataSet = categoryAxis.getDataSet();
		if(null == dataSet) return false;	

		float axisScreenHeight = getPlotScreenHeight(); // getAxisScreenHeight();
		float axisDataHeight = (float) dataAxis.getAxisRange();		
		float XSteps = getVerticalXSteps(dataSet.size() + 1);

		int barNumber = getDatasetSize(mDataSet); 
		int currNumber = 0;
		float[] ret = mFlatBar.getBarWidthAndMargin(XSteps, barNumber);
		if(null == ret||ret.length != 2)
		{
			Log.e(TAG,"分隔间距计算失败.");
			return false;
		}
		float barWidth = ret[0];
		float barInnerMargin = ret[1];
		float labelBarUseWidth = add(mul(barNumber , barWidth) , 
									 mul(sub(barNumber , 1) , barInnerMargin));
		
		// X 轴 即分类轴
		int size = mDataSet.size();
		for (int i = 0; i < size; i++) {
			// 得到分类对应的值数据集
			BarData bd = mDataSet.get(i);	
			
			List<Double> barValues = bd.getDataSet();
			if(null == barValues) continue ;
			
			//用于处理单独针对某些柱子指定颜色的情况
			List<Integer> barDataColor = bd.getDataColor();		
			
			// 设成对应的颜色
			int barDefualtColor = bd.getColor();
			mFlatBar.getBarPaint().setColor(barDefualtColor);
						
			// 画出分类对应的所有柱形
			for (int j = 0; j < barValues.size(); j++) {
				Double bv = barValues.get(j);
										
				setBarDataColor(mFlatBar.getBarPaint(),barDataColor,j,barDefualtColor);
			
				float vaxlen = (float) MathHelper.getInstance().sub(bv, dataAxis.getAxisMin());				
				float valuePostion = mul(axisScreenHeight, div( vaxlen,axisDataHeight ) );
						
				float currLableX = add(plotArea.getLeft() , mul((j + 1) , XSteps));
				float drawBarStartX = sub(currLableX , labelBarUseWidth / 2);				

				// 计算同分类多柱 形时，新柱形的起始X坐标
				drawBarStartX = add(drawBarStartX , add(barWidth,barInnerMargin)  * currNumber);		
				
				// 计算同分类多柱 形时，新柱形的结束X坐标
				float drawBarEndX = add(drawBarStartX , barWidth);
				
				// 画出柱形
				float topY = sub(plotArea.getBottom() , valuePostion);
				mFlatBar.renderBar(drawBarStartX,plotArea.getBottom(),drawBarEndX, topY,
									canvas);
				//保存位置
				saveBarRectFRecord(i,j,drawBarStartX + mMoveX,topY + mMoveY,
									    drawBarEndX  + mMoveX,plotArea.getBottom() + mMoveY); 
				
				// 在柱形的顶端显示上柱形的当前值
				mFlatBar.renderBarItemLabel(getFormatterItemLabel(bv),
						add(drawBarStartX , barWidth / 2),
						sub(plotArea.getBottom() , valuePostion), canvas);				
			}
			currNumber++;
		}

		//画竖向柱形图的定制线		
		if(null != mCustomLine)
		{
			mCustomLine.setVerticalPlot(dataAxis, plotArea, getAxisScreenHeight());
			mCustomLine.renderVerticalCustomlinesDataAxis(canvas);
		}
		return true;
	}
			
	protected float getDrawClipHorizontalBarXMargin()
	{
		float XMargin = 0.0f;
		if(dataAxis.isShowAxisLabels())
		{												
			if(dataAxis.getHorizontalTickAlign() != Align.LEFT)
			{
				//这个有可能会因为format而不准，但没找到更合适的方法
				String str =  Double.toString(dataAxis.getAxisMin());					
				XMargin = DrawHelper.getInstance().getTextWidth(
										dataAxis.getTickLabelPaint(), str); 
				
				if(dataAxis.getHorizontalTickAlign() == Align.CENTER)
				{
					XMargin = div(XMargin,2);
				}													
			}
		}
		return XMargin;
	}
	
	protected float getDrawClipHorizontalBarYMargin()
	{
		float yMargin = 0.0f;
		if(categoryAxis.isShowAxisLabels())
		{
			yMargin = DrawHelper.getInstance().getPaintFontHeight(
							categoryAxis.getTickLabelPaint() ) / 2;				
		}	
		return yMargin ;
	}
	
	/////////////////////////////////////////////
	@Override
	protected float getClipYMargin()
	{
		float yMargin = 0.0f;
		
		switch (mDirection) 
		{
		case HORIZONTAL: 
			yMargin = getDrawClipHorizontalBarXMargin();
			break;				
		case VERTICAL: 
			yMargin = getDrawClipVerticalYMargin();
			break;				
		}
		
		return yMargin;
	}
	
	@Override
	protected float getClipXMargin()
	{
		float xMargin = 0.0f;
		switch (mDirection) 
		{
		case HORIZONTAL: 
			xMargin = getDrawClipHorizontalBarYMargin();
			break;				
		case VERTICAL: 
			xMargin = getDrawClipVerticalXMargin();
			break;				
		}
		return xMargin;
	}
	
	
	//轴刻度
	protected void renderAxesTick(Canvas canvas)
	{						
		switch (mDirection) 
		{
		case HORIZONTAL: 
			renderHorizontalBarDataAxis(canvas);
			renderHorizontalBarCategoryAxis(canvas);
			break;				
		case VERTICAL: 
			drawClipDataAxisTick(canvas);
			drawClipCategoryAxisTick(canvas);
			break;				
		}
	}
		
	protected void drawClipDataAxis(Canvas canvas)
	{		
		switch (mDirection) 
		{
		case HORIZONTAL: 
			renderHorizontalBarDataAxis(canvas);
			break;				
		case VERTICAL: 
			renderVerticalBarDataAxis(canvas);	
			break;				
		}
	}
	
	protected void drawClipCategoryAxis(Canvas canvas)
	{		
		switch (mDirection) 
		{
		case HORIZONTAL: 
			renderHorizontalBarCategoryAxis(canvas);
			break;				
		case VERTICAL: 
			renderVerticalBarCategoryAxis(canvas);
			break;				
		}
	}
	
	protected void drawClipPlot(Canvas canvas)
	{		
		switch (mDirection) 
		{
		case HORIZONTAL: 
			renderHorizontalBar(canvas);
			break;				
		case VERTICAL: 
			renderVerticalBar(canvas);
			break;				
		}
	}
	
	protected void drawClipAxisLine(Canvas canvas)
	{				
		switch (mDirection) 
		{
		case HORIZONTAL: 			
			//Y轴 线
			dataAxis.renderAxis(canvas, plotArea.getLeft(), plotArea.getTop(),
										plotArea.getLeft(), plotArea.getBottom());							
			//X轴 线
			dataAxis.renderAxis(canvas, plotArea.getLeft(), plotArea.getBottom(),
										plotArea.getPlotRight(), plotArea.getBottom());	
			
			break;				
		case VERTICAL: 
			
			//Y轴 线
			dataAxis.renderAxis(canvas, plotArea.getLeft(), plotArea.getTop(),
										plotArea.getLeft(), plotArea.getBottom());							
			//X轴 线
			dataAxis.renderAxis(canvas, plotArea.getLeft(), plotArea.getBottom(),
										plotArea.getPlotRight(), plotArea.getBottom());	
			
			break;				
		}
	}
		
	protected void drawClipDataAxisTick(Canvas canvas)
	{
		drawDataAxisLabels(canvas,mDirection,mLstDataTick);		
		mLstDataTick.clear();
	}
	
	protected void drawClipCategoryAxisTick(Canvas canvas)
	{
		drawCategoryAxisLabels(canvas,mDirection,mLstCateTick);		
		mLstCateTick.clear();
	}
		
	protected void drawClipLegend(Canvas canvas)
	{
		plotLegend.renderBarKey(canvas, this.mDataSet);	
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
		
	protected int getDatasetSize(List<BarData> dataSource)
	{		
		 if(null == dataSource) return 0;
		 
		 int ret = dataSource.size();
		// X 轴 即分类轴
		for (int i = 0; i < dataSource.size(); i++) 
		{			
			BarData bd = dataSource.get(i);	
			List<Double> barValues = bd.getDataSet();
			
			if(barValues.size() == 1)
			{
				if( Double.compare( barValues.get(0) , dataAxis.getAxisMin()) == 0 )
					ret--;
			}
		}
		return ret;
	}
		
	
	/**
	 * 对于有为单个柱形设置颜色的情况，以这个函数来为画笔设置相应的颜色
	 * @param paint			柱形画笔
	 * @param lstDataColor	数据颜色集
	 * @param currNumber	当前序号
	 * @param defaultColor	默认的柱形颜色
	 */
	protected void setBarDataColor(Paint paint,
								   List<Integer> lstDataColor,
								   int currNumber,								  
								   int defaultColor)
	{		
		if(null != lstDataColor)
		{
			if( lstDataColor.size() > currNumber)
			{
				paint.setColor( lstDataColor.get(currNumber));	
			}else{
				paint.setColor( defaultColor);
			}		
		}		
	}
	
	/**
	 * 返回当前点击点的信息
	 * @param x 点击点X坐标
	 * @param y	点击点Y坐标
	 * @return 返回对应的位置记录
	 */
	public BarPosition getPositionRecord(float x,float y)
	{		
		return getBarRecord(x,y);
	}
	
	@Override
	protected boolean postRender(Canvas canvas) throws Exception 
	{		
		// 绘制图表
		try {			
			super.postRender(canvas);
			
			if(null == mDataSet)
			{
				Log.e(TAG,"数据轴数据源为空");
				return false;
			}
			
			// 得到分类轴数据集
			List<String> dataSet = categoryAxis.getDataSet();
			if(null == dataSet)
			{
				Log.e(TAG,"分类轴数据集为空.");
				return false;
			}
			
			boolean ret = true;
			
			if(getPanModeStatus())
			{
				switch (mDirection) {
					case HORIZONTAL: {											
						ret = drawClipHorizontalPlot(canvas);
						break;
					}
					case VERTICAL: {
						ret = drawClipVerticalPlot(canvas);				
						break;
					}
				}
			}else{
				ret = drawFixedPlot(canvas);
			}
						
			return ret;
		} catch (Exception e) {
			throw e;
		}
		
	}
	/////////////////////////////////////
	@Override
	public boolean render(Canvas canvas) throws Exception {
		// TODO Auto-generated method stubcalcPlotRange		
		try {			
				if (null == canvas)return false;
				super.render(canvas);	
				
				renderFocusShape(canvas);
				
				renderToolTip(canvas);
				return true;				
		} catch (Exception e) {
			throw e;
		}
	}
	
	///////////////////////////////////////
}
