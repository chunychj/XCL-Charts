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
 * @version v0.1
 */
package org.xclcharts.chart;


import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import org.xclcharts.renderer.LnChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.line.PlotDot;
import org.xclcharts.renderer.line.PlotDotRender;
import org.xclcharts.renderer.line.PlotLine;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;


/**
 * @ClassName AreaChart
 * @Description  面积图基类
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 */

public class AreaChart extends LnChart{	
	
	//画点分类的画笔
  	protected Paint mPaintAreaFill =  null; 
  	
    //数据源
  	protected List<AreaData> mDataset;
  	
  	//透明度
  	private int mAreaAlpha = 100;
	
	public AreaChart()
	{
		super();
		
		mPaintAreaFill = new Paint();
		mPaintAreaFill.setStyle(Style.FILL);
		mPaintAreaFill.setAntiAlias(true);
		mPaintAreaFill.setColor((int)Color.rgb(73, 172, 72));		
		
		categoryAxis.setHorizontalTickAlign(Align.CENTER);
		dataAxis.setHorizontalTickAlign(Align.LEFT);
		
	}
	
	 /**
	 * 分类轴的数据源
	 * @param categories 分类集
	 */
	public void setCategories(List<String> categories)
	{
		categoryAxis.setDataBuilding(categories);
	}
	
	/**
	 *  设置数据轴的数据源
	 * @param dataset 数据源
	 */
	public void setDataSource(List<AreaData> dataset)
	{
		this.mDataset = dataset;		
	}
	
	/**
	 * 设置透明度,默认为100
	 * @param alpha 透明度
	 */
	public void setAreaAlpha(int alpha)
	{
		mAreaAlpha = alpha;
	}	

	/**
	 * 绘制区域
	 * @param bd	数据序列
	 * @param type	绘制类型
	 * @param alpha 透明度
	 */
	private void renderLine(Canvas canvas, AreaData bd,String type,int alpha)
	{
		float initX =  plotArea.getLeft();
        float initY =  plotArea.getBottom();
         
		float lineStartX = initX;
        float lineStartY = initY;
        float lineEndX = 0.0f;
        float lineEndY = 0.0f;
        						
		float axisScreenHeight = getAxisScreenHeight();
		float axisDataHeight =  (float) dataAxis.getAxisRange();	
		float currLablesSteps = this.getAxisScreenWidth() / (categoryAxis.getDataSet().size() -1) ;  	
		
		//数据源
		List<Double> chartValues = bd.getLinePoint();
		 //用于画折线   
        Path pathArea = new Path();  
        pathArea.moveTo(initX,initY);   
        
        //透明度。其取值范围是0---255,数值越小，越透明，颜色上表现越淡             
        mPaintAreaFill.setAlpha( mAreaAlpha );            
        PlotLine pLine = bd.getPlotLine(); 
        //设置当前填充色
        mPaintAreaFill.setColor(bd.getAreaFillColor());
            
		int j = 0;					 
		for(Double bv : chartValues)
        {								
			//参数值与最大值的比例  照搬到 y轴高度与矩形高度的比例上来 	                                
        	float valuePosition = (float) Math.round(
					axisScreenHeight * ( (bv - dataAxis.getAxisMin() ) / axisDataHeight)) ; 
        	
        	if(j == 0 )
			{
				lineStartX = initX;
				lineStartY = initY - valuePosition;
				
				lineEndX = lineStartX;
				lineEndY = lineStartY;
			}else{
				lineEndX =  initX + (j) * currLablesSteps;
				lineEndY = initY - valuePosition;
			}
        	        	 
        	if(j == chartValues.size() - 1)    //收尾，将path连接一气  
            {  
        		// p.lineTo(lineEndX ,initY);                
        	}else{
        		pathArea.lineTo(lineEndX ,lineEndY);   
        	}
       
        	////////////////////
        	if(type.equalsIgnoreCase("LINE"))
        	{
        		canvas.drawLine( lineStartX ,lineStartY ,lineEndX ,lineEndY,
        												pLine.getLinePaint());            	
        	}else if(type.equalsIgnoreCase("DOT2LABEL")){
        		        		
        		if(!pLine.getDotStyle().equals(XEnum.DotStyle.HIDE))
            	{            	
            		PlotDot pDot = pLine.getPlotDot();	              
            		float rendEndX  = lineEndX  + pDot.getDotRadius();               		
        			
            		PlotDotRender.getInstance().renderDot(canvas,pDot,
            				lineStartX ,lineStartY ,
            				lineEndX ,lineEndY,
            				pLine.getDotPaint()); //标识图形            			                	
        			lineEndX = rendEndX;
            	}
        		
        		if(bd.getLabelVisible())
            	{        			
            		//fromatter
            		canvas.drawText(this.getFormatterItemLabel(bv) ,
							lineEndX, lineEndY,  pLine.getDotLabelPaint());
            	}
        	}else{
        		return ;
        	}      
        	////////////////////
        	
        	lineStartX = lineEndX;
			lineStartY = lineEndY;

			j++;
        }	
                	
		pathArea.lineTo(lineStartX ,lineStartY);  
		pathArea.lineTo(lineStartX ,initY);  
		pathArea.close(); 
		if(type.equalsIgnoreCase("LINE"))
    	{
			canvas.drawPath(pathArea, mPaintAreaFill);
    	}
	}
	
	
	private void renderVerticalPlot(Canvas canvas)
	{				
		renderVerticalDataAxis(canvas);
		renderVerticalCategoryAxis(canvas);
		
		List<LnData> lstKey = new ArrayList<LnData>();		
		//开始处 X 轴 即分类轴                  
		for(int i=0;i<mDataset.size();i++)
		{								
			this.renderLine(canvas, mDataset.get(i),"LINE",
						(int)Math.round(mDataset.size() *i));
			this.renderLine(canvas, mDataset.get(i),"DOT2LABEL",
						(int)Math.round(mDataset.size() *i));
			lstKey.add(mDataset.get(i));
		}
			
		plotKey.renderLineKey(canvas, lstKey);
	}
	

	
	@Override
	protected boolean postRender(Canvas canvas) throws Exception 
	{
		// 绘制图表
		try {
			super.postRender(canvas);
			
			//绘制图表
			renderVerticalPlot(canvas);
		} catch (Exception e) {
			throw e;
		}
		return true;
	}
	 
}
