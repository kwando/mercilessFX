package me.merciless.fx.lensfx;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import java.util.ArrayList;

/**
 *
 * @author kwando
 */
public class LensFlareFilter extends Filter {

  private float downSampleFactor = 2f;
  private float threshold = 0.75f;
  private float blurScale = 1.4f;
  private Vector3f chromatic = new Vector3f(0.1f, 0.2f, 0.3f).multLocal(0.07f);
  private Material thresholdMaterial;
  private Material radialBlurMaterial;
  private float flareDispersal = .2f;
  private float flareHaloWidth = .48f;
  private float amplification = 2.0f;

  @Override
  protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
    thresholdMaterial = new Material(manager, "me/merciless/fx/lensfx/Threshold.j3md");
    thresholdMaterial.setFloat("Threshold", threshold);

    radialBlurMaterial = new Material(manager, "me/merciless/fx/lensfx/RadialBlur.j3md");

    material = new Material(manager, "me/merciless/fx/lensfx/PassThrough.j3md");
    postRenderPasses = new ArrayList<Pass>();

    Format format = Format.RGBA8;
    final int width = (int) (w / downSampleFactor);
    final int height = (int) (h / downSampleFactor);
    final Pass thresholdPass = new Pass() {

      @Override
      public void beforeRender() {
        thresholdMaterial.setFloat("Threshold", threshold);
      }
      
      @Override
      public boolean requiresSceneAsTexture() {
        return true;
      }
    };
    thresholdPass.init(renderManager.getRenderer(), width, height, format, Format.Depth, 1, thresholdMaterial);
    postRenderPasses.add(thresholdPass);


    final Pass flarePass = new Pass() {
      @Override
      public boolean requiresSceneAsTexture() {
        return false;
      }

      @Override
      public void beforeRender() {
        Texture texture = thresholdPass.getRenderedTexture();
        radialBlurMaterial.setFloat("FlareHaloWidth", flareHaloWidth);
        radialBlurMaterial.setFloat("FlareDispersal", flareDispersal);
        radialBlurMaterial.setVector3("Chromatic", chromatic);
        radialBlurMaterial.setTexture("Texture", texture);
      }
    };
    flarePass.init(renderManager.getRenderer(), width, height, format, Format.Depth, 1, radialBlurMaterial);
    postRenderPasses.add(flarePass);




    // SETUP BLUR
    final Material hBlurMat = new Material(manager, "Common/MatDefs/Blur/HGaussianBlur.j3md");
    final Material vBlurMat = new Material(manager, "Common/MatDefs/Blur/VGaussianBlur.j3md");
    final Pass hblurPass = new Pass() {
      @Override
      public boolean requiresSceneAsTexture() {
        return false;
      }

      @Override
      public void beforeRender() {
        hBlurMat.setFloat("Size", height);
        hBlurMat.setFloat("Scale", blurScale);
        hBlurMat.setTexture("Texture", flarePass.getRenderedTexture());
      }
    };
    hblurPass.init(renderManager.getRenderer(), width, height, format, Format.Depth, 1, hBlurMat);
    postRenderPasses.add(hblurPass);

    final Pass vblurPass = new Pass() {
      @Override
      public boolean requiresSceneAsTexture() {
        return false;
      }

      @Override
      public void beforeRender() {
        vBlurMat.setTexture("Texture", hblurPass.getRenderedTexture());
        vBlurMat.setFloat("Size", width);
        vBlurMat.setFloat("Scale", blurScale);
      }
    };
    vblurPass.init(renderManager.getRenderer(), width, height, format, Format.Depth, 1, vBlurMat);
    postRenderPasses.add(vblurPass);


    material.setTexture("Overlay", vblurPass.getRenderedTexture());
    material.setTexture("Dirt", manager.loadTexture("me/merciless/fx/lensfx/lensdirt_lowc.png"));
  }

  @Override
  protected Material getMaterial() {
    material.setFloat("Amplification", amplification);
    return material;
  }

  public float getDownSampleFactor() {
    return downSampleFactor;
  }

  public void setDownSampleFactor(float downSampleFactor) {
    this.downSampleFactor = downSampleFactor;
  }

  public float getThreshold() {
    return threshold;
  }

  public void setThreshold(float threshold) {
    this.threshold = threshold;
  }

  public float getBlurScale() {
    return blurScale;
  }

  public void setBlurScale(float blurScale) {
    this.blurScale = blurScale;
  }

  public Vector3f getChromatic() {
    return chromatic;
  }

  public void setChromatic(Vector3f chromatic) {
    this.chromatic = chromatic;
  }

  public float getFlareDispersal() {
    return flareDispersal;
  }

  public void setFlareDispersal(float flareDispersal) {
    this.flareDispersal = flareDispersal;
  }

  public float getAmplification() {
    return amplification;
  }

  public void setAmplification(float amplification) {
    this.amplification = amplification;
  }
}
