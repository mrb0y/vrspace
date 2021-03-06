import { World } from '../../../babylon/vrspace-ui.js';

export class Kidville extends World {
  async createCamera() {
    // Add a camera to the scene and attach it to the scene
    this.camera = new BABYLON.UniversalCamera("UniversalCamera", new BABYLON.Vector3(-97.51, 10, 62.72), this.scene);
    this.camera.maxZ = 100000;
    this.camera.minZ = 0;
    this.camera.setTarget(new BABYLON.Vector3(0,0,0));
    // not required, world.init() does that
    //this.camera.attachControl(canvas, true);
    this.camera.applyGravity = true;
    this.camera.speed = 0.5;
    //Set the ellipsoid around the camera (e.g. your player's size)
    this.camera.ellipsoid = new BABYLON.Vector3(.5, 1, .5);
    this.camera.checkCollisions = true;
  }
  async createLights() {
    var light = new BABYLON.DirectionalLight("light", new BABYLON.Vector3(-1, -1, 0), this.scene);
    light.intensity = 2;
    var light1 = new BABYLON.HemisphericLight("light1", new BABYLON.Vector3(0, 1, 0), this.scene);
    return light1;
  }
  async createSkyBox() {
    var skybox = BABYLON.Mesh.CreateBox("skyBox", 10000, this.scene);
    var skyboxMaterial = new BABYLON.StandardMaterial("skyBox", this.scene);
    skyboxMaterial.backFaceCulling = false;
    skyboxMaterial.disableLighting = true;
    skybox.material = skyboxMaterial;
    skybox.infiniteDistance = true;
    skyboxMaterial.reflectionTexture = new BABYLON.CubeTexture("//www.babylonjs.com/assets/skybox/TropicalSunnyDay", this.scene);
    skyboxMaterial.reflectionTexture.coordinatesMode = BABYLON.Texture.SKYBOX_MODE;
    return skybox;
  }
  
  getFloorMeshes() {
    return [this.scene.getMeshByName('Object013')];
  }
  
  isSelectableMesh(mesh) {
    return mesh.name == "node167" || mesh.name == "node3" || mesh.name == "node30" || mesh.name == "node31";
  }
  
  loaded(file, mesh) {
    mesh.scaling = new BABYLON.Vector3(0.5,0.5,0.5);
  }
  
}

export const WORLD = new Kidville();