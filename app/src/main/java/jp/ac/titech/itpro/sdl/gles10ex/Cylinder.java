package jp.ac.titech.itpro.sdl.gles10ex;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.lang.Math;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class Cylinder implements SimpleRenderer.Obj {

    private FloatBuffer vbuf;
    private FloatBuffer nbuf;
    private ByteBuffer ibuf;
    private float x, y, z, h;
    private int d;

    public Cylinder(float r, float h, int d, float x, float y, float z) {
        // 頂点と法線計算
        ArrayList<Float> vert = new ArrayList<>();
        ArrayList<Float> norm = new ArrayList<>();
        // 下　(簡単のため中心も追加)
        vert.add(0.0f);
        vert.add(0.0f);
        vert.add(0.0f);
        norm.add(0.0f);
        norm.add(-1.0f);
        norm.add(0.0f);
        for(int i = 0; i < d; ++i) {
            vert.add((float)Math.cos(Math.toRadians(i * 360.f / d)) * r);
            vert.add(0.0f);
            vert.add((float)Math.sin(Math.toRadians(i * 360.f / d)) * r);
            norm.add(0.0f);
            norm.add(-1.0f);
            norm.add(0.0f);
        }
        // 側面 (同じ頂点でも法線が異なるため複製)
        for(int k = 0; k < 2; ++k) {
            for(int i = 0; i < d; ++i) {
                float nx = (float)Math.cos(Math.toRadians(i * 360.f / d));
                float nz = (float)Math.sin(Math.toRadians(i * 360.f / d));
                vert.add(nx * r);
                vert.add(h * k);
                vert.add(nz * r);
                norm.add(nx);
                norm.add(0.0f);
                norm.add(nz);
            }
        }
        // 上
        vert.add(0.0f);
        vert.add(h);
        vert.add(0.0f);
        norm.add(0.0f);
        norm.add(1.0f);
        norm.add(0.0f);
        for(int i = 0; i < d; ++i) {
            vert.add((float)Math.cos(Math.toRadians(i * 360.f / d)) * r);
            vert.add(h);
            vert.add((float)Math.sin(Math.toRadians(i * 360.f / d)) * r);
            norm.add(0.0f);
            norm.add(1.0f);
            norm.add(0.0f);
        }

        float[] vertices = new float[vert.size()];
        for(int i = 0; i < vert.size(); ++i) { vertices[i] = vert.get(i); }
        vbuf = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vbuf.put(vertices);
        vbuf.position(0);

        float[] normals = new float[norm.size()];
        for(int i = 0; i < norm.size(); ++i) { normals[i] = norm.get(i); }
        nbuf = ByteBuffer.allocateDirect(normals.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        nbuf.put(normals);
        nbuf.position(0);

        // インデックスの計算
        ArrayList<Integer> ind = new ArrayList<>();
        int sideBase = d + 1;
        int upperBase = d * 3 + 1;
        // 下面
        for(int i = 0; i < d; ++i) {
            ind.add(0);
            ind.add(i + 1);
            ind.add((i+1)%d + 1);
        }
        // 側面
        for(int i = 0; i < d; ++i) {
            ind.add(sideBase + i);
            ind.add(sideBase + (i+1)%d);
            ind.add(sideBase + d + i);
            ind.add(sideBase + d + i);
            ind.add(sideBase + (i+1)%d);
            ind.add(sideBase + d + (i+1)%d);
        }
        // 上面
        for(int i = 0; i < d; ++i) {
            ind.add(upperBase);
            ind.add(upperBase + i + 1);
            ind.add(upperBase + (i+1)%d + 1);
        }

        // インデックスバッファ
        byte[] indices = new byte[ind.size()];
        for(int i = 0; i < ind.size(); ++i) { indices[i] = (byte)((int)ind.get(i)); }
        ibuf = ByteBuffer.allocateDirect(indices.length).order(
                ByteOrder.nativeOrder());
        ibuf.put(indices).position(0);

        this.d = d;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void draw(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vbuf);

        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, nbuf);

        gl.glDrawElements(GL10.GL_TRIANGLES, d * 12, GL10.GL_UNSIGNED_BYTE, ibuf);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getZ() {
        return z;
    }
}
