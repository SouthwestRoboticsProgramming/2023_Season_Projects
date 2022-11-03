(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-5af10b37"],{1029:function(t,e,i){"use strict";var s=function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",[i("v-row",{attrs:{dense:"",align:"center"}},[i("v-col",{attrs:{cols:2}},[i("span",[t._v(t._s(t.name))])]),i("v-col",{attrs:{cols:10}},[i("v-range-slider",{staticClass:"align-center",attrs:{value:t.localValue,max:t.max,min:t.min,"hide-details":"",dark:"",color:"#4baf62",step:t.step},on:{input:t.handleInput},scopedSlots:t._u([{key:"prepend",fn:function(){return[i("v-text-field",{staticClass:"mt-0 pt-0",staticStyle:{width:"50px"},attrs:{dark:"",value:t.localValue[0],max:t.max,min:t.min,"hide-details":"","single-line":"",type:"number",step:t.step},on:{input:t.handleChange,focus:function(e){t.prependFocused=!0},blur:function(e){t.prependFocused=!1}}})]},proxy:!0},{key:"append",fn:function(){return[i("v-text-field",{staticClass:"mt-0 pt-0",staticStyle:{width:"50px"},attrs:{dark:"",value:t.localValue[1],max:t.max,min:t.min,"hide-details":"","single-line":"",type:"number",step:t.step},on:{input:t.handleChange,focus:function(e){t.appendFocused=!0},blur:function(e){t.appendFocused=!1}}})]},proxy:!0}])})],1)],1)],1)},a=[],n={name:"RangeSlider",props:["name","min","max","value","step"],data(){return{prependFocused:!1,appendFocused:!1}},methods:{handleChange(t){let e=0;!1===this.prependFocused&&!0===this.appendFocused&&(e=1),(this.prependFocused||this.appendFocused)&&this.$set(this.localValue,e,t)},handleInput(t){this.prependFocused&&this.appendFocused||(this.localValue=t)}},computed:{localValue:{get(){return this.value},set(t){this.$emit("input",t)}}}},l=n,r=i("2877"),o=i("6544"),h=i.n(o),u=i("62ad"),c=(i("33e9"),i("9a18")),d=i("80d2"),p=c["a"].extend({name:"v-range-slider",props:{value:{type:Array,default:()=>[0,0]}},data(){return{activeThumb:null,lazyValue:this.value}},computed:{classes(){return{...c["a"].options.computed.classes.call(this),"v-input--range-slider":!0}},internalValue:{get(){return this.lazyValue},set(t){let e=t.map(t=>this.roundValue(Math.min(Math.max(t,this.minValue),this.maxValue)));if(e[0]>e[1]||e[1]<e[0]){if(null!==this.activeThumb){const t=1===this.activeThumb?0:1,e=this.$refs[`thumb_${t}`];e.focus()}e=[e[1],e[0]]}this.lazyValue=e,Object(d["i"])(e,this.value)||this.$emit("input",e),this.validate()}},inputWidth(){return this.internalValue.map(t=>(this.roundValue(t)-this.minValue)/(this.maxValue-this.minValue)*100)},trackFillStyles(){const t=c["a"].options.computed.trackFillStyles.call(this),e=Math.abs(this.inputWidth[0]-this.inputWidth[1]),i=this.vertical?"height":"width",s=this.vertical?this.$vuetify.rtl?"top":"bottom":this.$vuetify.rtl?"right":"left";return t[i]=`${e}%`,t[s]=`${this.inputWidth[0]}%`,t}},methods:{getTrackStyle(t,e,i=0,s=0){const a=this.vertical?this.$vuetify.rtl?"top":"bottom":this.$vuetify.rtl?"right":"left",n=this.vertical?"height":"width",l=`calc(${t}% + ${i}px)`,r=`calc(${e}% + ${s}px)`;return{transition:this.trackTransition,[a]:l,[n]:r}},getIndexOfClosestValue(t,e){return Math.abs(t[0]-e)<Math.abs(t[1]-e)?0:1},genInput(){return Object(d["g"])(2).map(t=>{const e=c["a"].options.methods.genInput.call(this);return e.data=e.data||{},e.data.attrs=e.data.attrs||{},e.data.attrs.value=this.internalValue[t],e})},genTrackContainer(){const t=[];if(this.disabled){const e=10,i=[[0,this.inputWidth[0],0,-e],[this.inputWidth[0],Math.abs(this.inputWidth[1]-this.inputWidth[0]),e,-2*e],[this.inputWidth[1],Math.abs(100-this.inputWidth[1]),e,0]];this.$vuetify.rtl&&i.reverse(),t.push(...i.map(t=>this.$createElement("div",this.setBackgroundColor(this.computedTrackColor,{staticClass:"v-slider__track-background",style:this.getTrackStyle(...t)}))))}else t.push(this.$createElement("div",this.setBackgroundColor(this.computedTrackColor,{staticClass:"v-slider__track-background",style:this.getTrackStyle(0,100)})),this.$createElement("div",this.setBackgroundColor(this.computedColor,{staticClass:"v-slider__track-fill",style:this.trackFillStyles})));return this.$createElement("div",{staticClass:"v-slider__track-container",ref:"track"},t)},genChildren(){return[this.genInput(),this.genTrackContainer(),this.genSteps(),Object(d["g"])(2).map(t=>{const e=this.internalValue[t],i=e=>{this.isActive=!0,this.activeThumb=t,this.onThumbMouseDown(e)},s=e=>{this.isFocused=!0,this.activeThumb=t,this.$emit("focus",e)},a=t=>{this.isFocused=!1,this.activeThumb=null,this.$emit("blur",t)},n=this.inputWidth[t],l=this.isActive&&this.activeThumb===t,r=this.isFocused&&this.activeThumb===t;return this.genThumbContainer(e,n,l,r,i,s,a,`thumb_${t}`)})]},onSliderClick(t){if(!this.isActive){if(this.noClick)return void(this.noClick=!1);const{value:e,isInsideTrack:i}=this.parseMouseMove(t);if(i){this.activeThumb=this.getIndexOfClosestValue(this.internalValue,e);const t=`thumb_${this.activeThumb}`,i=this.$refs[t];i.focus()}this.setInternalValue(e),this.$emit("change",this.internalValue)}},onMouseMove(t){const{value:e,isInsideTrack:i}=this.parseMouseMove(t);i&&null===this.activeThumb&&(this.activeThumb=this.getIndexOfClosestValue(this.internalValue,e)),this.setInternalValue(e)},onKeyDown(t){if(null===this.activeThumb)return;const e=this.parseKeyDown(t,this.internalValue[this.activeThumb]);null!=e&&(this.setInternalValue(e),this.$emit("change",this.internalValue))},setInternalValue(t){this.internalValue=this.internalValue.map((e,i)=>i===this.activeThumb?t:Number(e))}}}),v=i("0fd9"),m=i("8654"),b=Object(r["a"])(l,s,a,!1,null,"312a6c2b",null);e["a"]=b.exports;h()(b,{VCol:u["a"],VRangeSlider:p,VRow:v["a"],VTextField:m["a"]})},"1a40":function(t,e,i){"use strict";i.r(e);var s=function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",[i("CVrangeSlider",{attrs:{name:"Hue",min:0,max:180},on:{input:function(e){return t.handleData("hue")}},model:{value:t.value.hue,callback:function(e){t.$set(t.value,"hue",e)},expression:"value.hue"}}),i("CVrangeSlider",{attrs:{name:"Saturation",min:0,max:255},on:{input:function(e){return t.handleData("saturation")}},model:{value:t.value.saturation,callback:function(e){t.$set(t.value,"saturation",e)},expression:"value.saturation"}}),i("CVrangeSlider",{attrs:{name:"Value",min:0,max:255},on:{input:function(e){return t.handleData("value")}},model:{value:t.value.value,callback:function(e){t.$set(t.value,"value",e)},expression:"value.value"}}),i("v-divider",{staticStyle:{"margin-top":"5px"},attrs:{color:"darkgray "}}),i("v-btn",{staticStyle:{margin:"20px"},attrs:{color:"#4baf62",small:""},on:{click:function(e){return t.setFunction(1)}}},[i("v-icon",[t._v("colorize")]),t._v("\n        Eye drop\n    ")],1),i("v-btn",{staticStyle:{margin:"20px"},attrs:{color:"#4baf62",small:""},on:{click:function(e){return t.setFunction(2)}}},[i("v-icon",[t._v("add")]),t._v("\n        Expand Selection\n    ")],1),i("v-btn",{staticStyle:{margin:"20px"},attrs:{color:"#4baf62",small:""},on:{click:function(e){return t.setFunction(3)}}},[i("v-icon",[t._v("remove")]),t._v("\n        Shrink Selection\n    ")],1),i("v-divider",{attrs:{color:"darkgray "}}),i("CVswitch",{attrs:{name:"Erode"},on:{input:function(e){return t.handleData("erode")}},model:{value:t.value.erode,callback:function(e){t.$set(t.value,"erode",e)},expression:"value.erode"}}),i("CVswitch",{attrs:{name:"Dilate"},on:{input:function(e){return t.handleData("dilate")}},model:{value:t.value.dilate,callback:function(e){t.$set(t.value,"dilate",e)},expression:"value.dilate"}})],1)},a=[],n=i("1029"),l=i("b530"),r={name:"Threshold",props:["value"],components:{CVrangeSlider:n["a"],CVswitch:l["a"]},data(){return{currentFunction:void 0,colorPicker:void 0,currentBinaryState:0}},computed:{pipeline:{get(){return this.$store.state.pipeline}},driverState:{get(){return this.$store.state.driverMode},set(t){this.$store.commit("driverMode",t)}}},methods:{onClick(t){if(void 0!==this.currentFunction){let e=this.colorPicker.colorPickerClick(t,this.currentFunction,[[this.value.hue[0],this.value.saturation[0],this.value.value[0]],[this.value.hue[1],this.value.saturation[1],this.value.value[1]]]);this.currentFunction=void 0,this.value.hue=[e[0][0],e[1][0]],this.value.saturation=[e[0][1],e[1][1]],this.value.value=[e[0][2],e[1][2]],this.value.isBinary=this.currentBinaryState;let i=this.$msgPack.encode({hue:this.value.hue,saturation:this.value.saturation,value:this.value.value,isBinary:this.value.isBinary});this.$socket.send(i),this.$emit("update")}},setFunction(t){switch(this.currentBinaryState=this.value.isBinary,!0===this.currentBinaryState&&(this.value.isBinary=!1,this.handleData("isBinary")),t){case 0:this.currentFunction=void 0;break;case 1:this.currentFunction=this.colorPicker.eyeDrop;break;case 2:this.currentFunction=this.colorPicker.expand;break;case 3:this.currentFunction=this.colorPicker.shrink;break}},handleData(t){this.handleInput(t,this.value[t]),this.$emit("update")}},mounted:function(){const t=this;this.colorPicker=i("b3e4").default,this.$nextTick(()=>{t.colorPicker.initColorPicker()})}},o=r,h=i("2877"),u=i("6544"),c=i.n(u),d=i("8336"),p=i("ce7e"),v=i("132d"),m=Object(h["a"])(o,s,a,!1,null,"4befc777",null);e["default"]=m.exports;c()(m,{VBtn:d["a"],VDivider:p["a"],VIcon:v["a"]})},"33e9":function(t,e,i){},5311:function(t,e,i){"use strict";var s=i("5607"),a=i("2b0e");e["a"]=a["a"].extend({name:"rippleable",directives:{ripple:s["a"]},props:{ripple:{type:[Boolean,Object],default:!0}},methods:{genRipple(t={}){return this.ripple?(t.staticClass="v-input--selection-controls__ripple",t.directives=t.directives||[],t.directives.push({name:"ripple",value:{center:!0}}),t.on=Object.assign({click:this.onChange},this.$listeners),this.$createElement("div",t)):null},onChange(){}}})},"9a18":function(t,e,i){"use strict";var s=i("ba0d");e["a"]=s["a"]},"9d01":function(t,e,i){},"9e29":function(t,e,i){},a293:function(t,e,i){"use strict";function s(){return!1}function a(t,e,i){i.args=i.args||{};const a=i.args.closeConditional||s;if(!t||!1===a(t))return;if("isTrusted"in t&&!t.isTrusted||"pointerType"in t&&!t.pointerType)return;const n=(i.args.include||(()=>[]))();n.push(e),!n.some(e=>e.contains(t.target))&&setTimeout(()=>{a(t)&&i.value&&i.value(t)},0)}const n={inserted(t,e){const i=i=>a(i,t,e),s=document.querySelector("[data-app]")||document.body;s.addEventListener("click",i,!0),t._clickOutside=i},unbind(t){if(!t._clickOutside)return;const e=document.querySelector("[data-app]")||document.body;e&&e.removeEventListener("click",t._clickOutside,!0),delete t._clickOutside}};e["a"]=n},b3e4:function(t,e,i){"use strict";i.r(e);var s=void 0,a=void 0;function n(){s=document.createElement("canvas"),a=document.getElementById("CameraStream"),s.width=a.width,s.height=a.height}function l(t,e,i){let n=a.getBoundingClientRect(),l=Math.round(t.clientX-n.left),r=Math.round(t.clientY-n.top),o=s.getContext("2d");o.drawImage(a,0,0,a.width,a.height);let h=o.getImageData(l,r,1,1).data;if(void 0!==e)return e(h,i)}function r(t){let e=u(t),i=d([e,e.slice(0)]);return i}function o(t,e){let i=u(t),s=d([[].concat(i),i]);return c(e.concat(s))}function h(t,e){let i=u(t),s=d([[].concat(i),i]);return p(e,s[0])||p(e,s[1]),e}function u(t){let e=t[0],i=t[1],s=t[2];e/=255,i/=255,s/=255;let a=Math.min(e,Math.min(i,s)),n=Math.max(e,Math.max(i,s)),l=e===a?i-s:s===a?e-i:s-e,r=e===a?3:s===a?1:5,o=30*(r-l/(n-a)),h=255*(n-a)/n,u=255*n;return isNaN(o)&&(o=0),isNaN(h)&&(h=0),isNaN(u)&&(u=0),[Math.round(o),Math.round(h),Math.round(u)]}function c(t){let e=[[],[]];for(var i=0;i<3;i++){e[0][i]=t[0][i],e[1][i]=t[0][i];for(var s=t.length-1;s>=0;s--)e[0][i]=Math.min(t[s][i],e[0][i]),e[1][i]=Math.max(t[s][i],e[1][i])}return e}function d(t){let e=[[],[]];for(let i=0;i<3;i++)e[0][i]=Math.max(0,t[0][i]-10),e[1][i]=Math.min(255,t[1][i]+10);return e[1][0]=Math.min(180,e[1][0]),e}function p(t,e){let i=!0;for(let s=0;s<e.length&&i;s++)t[0][s]<=e[s]<=t[1][s]||(i=!1);if(i)for(let s=0;s<e.length;s++)e[s]-t[0][s]<t[1][s]-e[s]?t[0][s]=Math.min(t[0][s]+10,t[1][s]):t[1][s]=Math.max(t[1][s]-10,t[0][s]);return i}e["default"]={initColorPicker:n,colorPickerClick:l,eyeDrop:r,expand:o,shrink:h}},b530:function(t,e,i){"use strict";var s=function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",[i("v-row",{attrs:{dense:"",align:"center"}},[i("v-col",{attrs:{cols:2}},[i("span",[t._v(t._s(t.name))])]),i("v-col",[i("v-switch",{attrs:{dark:"",disabled:t.disabled,color:"#4baf62"},model:{value:t.localValue,callback:function(e){t.localValue=e},expression:"localValue"}})],1)],1)],1)},a=[],n={name:"CVSwitch",props:["name","value","disabled"],data(){return{}},computed:{localValue:{get(){return this.value},set(t){this.$emit("input",t)}}}},l=n,r=i("2877"),o=i("6544"),h=i.n(o),u=i("62ad"),c=i("0fd9"),d=(i("ec29"),i("9d01"),i("fe09")),p=i("c37a"),v=i("c3f0"),m=i("0789"),b=i("490a"),f=i("80d2"),g=d["a"].extend({name:"v-switch",directives:{Touch:v["a"]},props:{inset:Boolean,loading:{type:[Boolean,String],default:!1},flat:{type:Boolean,default:!1}},computed:{classes(){return{...p["a"].options.computed.classes.call(this),"v-input--selection-controls v-input--switch":!0,"v-input--switch--flat":this.flat,"v-input--switch--inset":this.inset}},attrs(){return{"aria-checked":String(this.isActive),"aria-disabled":String(this.disabled),role:"switch"}},validationState(){return this.hasError&&this.shouldValidate?"error":this.hasSuccess?"success":null!==this.hasColor?this.computedColor:void 0},switchData(){return this.setTextColor(this.loading?void 0:this.validationState,{class:this.themeClasses})}},methods:{genDefaultSlot(){return[this.genSwitch(),this.genLabel()]},genSwitch(){return this.$createElement("div",{staticClass:"v-input--selection-controls__input"},[this.genInput("checkbox",{...this.attrs,...this.attrs$}),this.genRipple(this.setTextColor(this.validationState,{directives:[{name:"touch",value:{left:this.onSwipeLeft,right:this.onSwipeRight}}]})),this.$createElement("div",{staticClass:"v-input--switch__track",...this.switchData}),this.$createElement("div",{staticClass:"v-input--switch__thumb",...this.switchData},[this.genProgress()])])},genProgress(){return this.$createElement(m["c"],{},[!1===this.loading?null:this.$slots.progress||this.$createElement(b["a"],{props:{color:!0===this.loading||""===this.loading?this.color||"primary":this.loading,size:16,width:2,indeterminate:!0}})])},onSwipeLeft(){this.isActive&&this.onChange()},onSwipeRight(){this.isActive||this.onChange()},onKeydown(t){(t.keyCode===f["s"].left&&this.isActive||t.keyCode===f["s"].right&&!this.isActive)&&this.onChange()}}}),k=Object(r["a"])(l,s,a,!1,null,"6b5ccb52",null);e["a"]=k.exports;h()(k,{VCol:u["a"],VRow:c["a"],VSwitch:g})},ba0d:function(t,e,i){"use strict";i("9e29");var s=i("c37a"),a=i("0789"),n=i("58df"),l=i("297c"),r=i("a293"),o=i("80d2"),h=i("d9bd");e["a"]=Object(n["a"])(s["a"],l["a"]).extend({name:"v-slider",directives:{ClickOutside:r["a"]},mixins:[l["a"]],props:{disabled:Boolean,inverseLabel:Boolean,max:{type:[Number,String],default:100},min:{type:[Number,String],default:0},step:{type:[Number,String],default:1},thumbColor:String,thumbLabel:{type:[Boolean,String],default:void 0,validator:t=>"boolean"===typeof t||"always"===t},thumbSize:{type:[Number,String],default:32},tickLabels:{type:Array,default:()=>[]},ticks:{type:[Boolean,String],default:!1,validator:t=>"boolean"===typeof t||"always"===t},tickSize:{type:[Number,String],default:2},trackColor:String,trackFillColor:String,value:[Number,String],vertical:Boolean},data:()=>({app:null,oldValue:null,keyPressed:0,isFocused:!1,isActive:!1,noClick:!1}),computed:{classes(){return{...s["a"].options.computed.classes.call(this),"v-input__slider":!0,"v-input__slider--vertical":this.vertical,"v-input__slider--inverse-label":this.inverseLabel}},internalValue:{get(){return this.lazyValue},set(t){t=isNaN(t)?this.minValue:t;const e=this.roundValue(Math.min(Math.max(t,this.minValue),this.maxValue));e!==this.lazyValue&&(this.lazyValue=e,this.$emit("input",e))}},trackTransition(){return this.keyPressed>=2?"none":""},minValue(){return parseFloat(this.min)},maxValue(){return parseFloat(this.max)},stepNumeric(){return this.step>0?parseFloat(this.step):0},inputWidth(){const t=(this.roundValue(this.internalValue)-this.minValue)/(this.maxValue-this.minValue)*100;return t},trackFillStyles(){const t=this.vertical?"bottom":"left",e=this.vertical?"top":"right",i=this.vertical?"height":"width",s=this.$vuetify.rtl?"auto":"0",a=this.$vuetify.rtl?"0":"auto",n=this.disabled?`calc(${this.inputWidth}% - 10px)`:`${this.inputWidth}%`;return{transition:this.trackTransition,[t]:s,[e]:a,[i]:n}},trackStyles(){const t=this.vertical?this.$vuetify.rtl?"bottom":"top":this.$vuetify.rtl?"left":"right",e=this.vertical?"height":"width",i="0px",s=this.disabled?`calc(${100-this.inputWidth}% - 10px)`:`calc(${100-this.inputWidth}%)`;return{transition:this.trackTransition,[t]:i,[e]:s}},showTicks(){return this.tickLabels.length>0||!(this.disabled||!this.stepNumeric||!this.ticks)},numTicks(){return Math.ceil((this.maxValue-this.minValue)/this.stepNumeric)},showThumbLabel(){return!this.disabled&&!(!this.thumbLabel&&!this.$scopedSlots["thumb-label"])},computedTrackColor(){if(!this.disabled)return this.trackColor?this.trackColor:this.isDark?this.validationState:this.validationState||"primary lighten-3"},computedTrackFillColor(){if(!this.disabled)return this.trackFillColor?this.trackFillColor:this.validationState||this.computedColor},computedThumbColor(){return this.thumbColor?this.thumbColor:this.validationState||this.computedColor}},watch:{min(t){const e=parseFloat(t);e>this.internalValue&&this.$emit("input",e)},max(t){const e=parseFloat(t);e<this.internalValue&&this.$emit("input",e)},value:{handler(t){this.internalValue=t}}},beforeMount(){this.internalValue=this.value},mounted(){this.app=document.querySelector("[data-app]")||Object(h["c"])("Missing v-app or a non-body wrapping element with the [data-app] attribute",this)},methods:{genDefaultSlot(){const t=[this.genLabel()],e=this.genSlider();return this.inverseLabel?t.unshift(e):t.push(e),t.push(this.genProgress()),t},genSlider(){return this.$createElement("div",{class:{"v-slider":!0,"v-slider--horizontal":!this.vertical,"v-slider--vertical":this.vertical,"v-slider--focused":this.isFocused,"v-slider--active":this.isActive,"v-slider--disabled":this.disabled,"v-slider--readonly":this.readonly,...this.themeClasses},directives:[{name:"click-outside",value:this.onBlur}],on:{click:this.onSliderClick}},this.genChildren())},genChildren(){return[this.genInput(),this.genTrackContainer(),this.genSteps(),this.genThumbContainer(this.internalValue,this.inputWidth,this.isActive,this.isFocused,this.onThumbMouseDown,this.onFocus,this.onBlur)]},genInput(){return this.$createElement("input",{attrs:{value:this.internalValue,id:this.computedId,disabled:this.disabled,readonly:!0,tabindex:-1,...this.$attrs}})},genTrackContainer(){const t=[this.$createElement("div",this.setBackgroundColor(this.computedTrackColor,{staticClass:"v-slider__track-background",style:this.trackStyles})),this.$createElement("div",this.setBackgroundColor(this.computedTrackFillColor,{staticClass:"v-slider__track-fill",style:this.trackFillStyles}))];return this.$createElement("div",{staticClass:"v-slider__track-container",ref:"track"},t)},genSteps(){if(!this.step||!this.showTicks)return null;const t=parseFloat(this.tickSize),e=Object(o["g"])(this.numTicks+1),i=this.vertical?"bottom":"left",s=this.vertical?"right":"top";this.vertical&&e.reverse();const a=e.map(e=>{const a=this.$vuetify.rtl?this.maxValue-e:e,n=[];this.tickLabels[a]&&n.push(this.$createElement("div",{staticClass:"v-slider__tick-label"},this.tickLabels[a]));const l=e*(100/this.numTicks),r=this.$vuetify.rtl?100-this.inputWidth<l:l<this.inputWidth;return this.$createElement("span",{key:e,staticClass:"v-slider__tick",class:{"v-slider__tick--filled":r},style:{width:`${t}px`,height:`${t}px`,[i]:`calc(${l}% - ${t/2}px)`,[s]:`calc(50% - ${t/2}px)`}},n)});return this.$createElement("div",{staticClass:"v-slider__ticks-container",class:{"v-slider__ticks-container--always-show":"always"===this.ticks||this.tickLabels.length>0}},a)},genThumbContainer(t,e,i,s,a,n,l,r="thumb"){const o=[this.genThumb()],h=this.genThumbLabelContent(t);return this.showThumbLabel&&o.push(this.genThumbLabel(h)),this.$createElement("div",this.setTextColor(this.computedThumbColor,{ref:r,staticClass:"v-slider__thumb-container",class:{"v-slider__thumb-container--active":i,"v-slider__thumb-container--focused":s,"v-slider__thumb-container--show-label":this.showThumbLabel},style:this.getThumbContainerStyles(e),attrs:{role:"slider",tabindex:this.disabled||this.readonly?-1:this.$attrs.tabindex?this.$attrs.tabindex:0,"aria-label":this.label,"aria-valuemin":this.min,"aria-valuemax":this.max,"aria-valuenow":this.internalValue,"aria-readonly":String(this.readonly),"aria-orientation":this.vertical?"vertical":"horizontal",...this.$attrs},on:{focus:n,blur:l,keydown:this.onKeyDown,keyup:this.onKeyUp,touchstart:a,mousedown:a}}),o)},genThumbLabelContent(t){return this.$scopedSlots["thumb-label"]?this.$scopedSlots["thumb-label"]({value:t}):[this.$createElement("span",[String(t)])]},genThumbLabel(t){const e=Object(o["f"])(this.thumbSize),i=this.vertical?`translateY(20%) translateY(${Number(this.thumbSize)/3-1}px) translateX(55%) rotate(135deg)`:"translateY(-20%) translateY(-12px) translateX(-50%) rotate(45deg)";return this.$createElement(a["e"],{props:{origin:"bottom center"}},[this.$createElement("div",{staticClass:"v-slider__thumb-label-container",directives:[{name:"show",value:this.isFocused||this.isActive||"always"===this.thumbLabel}]},[this.$createElement("div",this.setBackgroundColor(this.computedThumbColor,{staticClass:"v-slider__thumb-label",style:{height:e,width:e,transform:i}}),[this.$createElement("div",t)])])])},genThumb(){return this.$createElement("div",this.setBackgroundColor(this.computedThumbColor,{staticClass:"v-slider__thumb"}))},getThumbContainerStyles(t){const e=this.vertical?"top":"left";let i=this.$vuetify.rtl?100-t:t;return i=this.vertical?100-i:i,{transition:this.trackTransition,[e]:`${i}%`}},onThumbMouseDown(t){this.oldValue=this.internalValue,this.keyPressed=2,this.isActive=!0;const e=!o["w"]||{passive:!0,capture:!0},i=!!o["w"]&&{passive:!0};"touches"in t?(this.app.addEventListener("touchmove",this.onMouseMove,i),Object(o["a"])(this.app,"touchend",this.onSliderMouseUp,e)):(this.app.addEventListener("mousemove",this.onMouseMove,i),Object(o["a"])(this.app,"mouseup",this.onSliderMouseUp,e)),this.$emit("start",this.internalValue)},onSliderMouseUp(t){t.stopPropagation(),this.keyPressed=0;const e=!!o["w"]&&{passive:!0};this.app.removeEventListener("touchmove",this.onMouseMove,e),this.app.removeEventListener("mousemove",this.onMouseMove,e),this.$emit("end",this.internalValue),Object(o["i"])(this.oldValue,this.internalValue)||(this.$emit("change",this.internalValue),this.noClick=!0),this.isActive=!1},onMouseMove(t){const{value:e}=this.parseMouseMove(t);this.internalValue=e},onKeyDown(t){if(this.disabled||this.readonly)return;const e=this.parseKeyDown(t,this.internalValue);null!=e&&(this.internalValue=e,this.$emit("change",e))},onKeyUp(){this.keyPressed=0},onSliderClick(t){if(this.noClick)return void(this.noClick=!1);const e=this.$refs.thumb;e.focus(),this.onMouseMove(t),this.$emit("change",this.internalValue)},onBlur(t){this.isFocused=!1,this.$emit("blur",t)},onFocus(t){this.isFocused=!0,this.$emit("focus",t)},parseMouseMove(t){const e=this.vertical?"top":"left",i=this.vertical?"height":"width",s=this.vertical?"clientY":"clientX",{[e]:a,[i]:n}=this.$refs.track.getBoundingClientRect(),l="touches"in t?t.touches[0][s]:t[s];let r=Math.min(Math.max((l-a)/n,0),1)||0;this.vertical&&(r=1-r),this.$vuetify.rtl&&(r=1-r);const o=l>=a&&l<=a+n,h=parseFloat(this.min)+r*(this.maxValue-this.minValue);return{value:h,isInsideTrack:o}},parseKeyDown(t,e){if(this.disabled)return;const{pageup:i,pagedown:s,end:a,home:n,left:l,right:r,down:h,up:u}=o["s"];if(![i,s,a,n,l,r,h,u].includes(t.keyCode))return;t.preventDefault();const c=this.stepNumeric||1,d=(this.maxValue-this.minValue)/c;if([l,r,h,u].includes(t.keyCode)){this.keyPressed+=1;const i=this.$vuetify.rtl?[l,u]:[r,u],s=i.includes(t.keyCode)?1:-1,a=t.shiftKey?3:t.ctrlKey?2:1;e+=s*c*a}else if(t.keyCode===n)e=this.minValue;else if(t.keyCode===a)e=this.maxValue;else{const i=t.keyCode===s?1:-1;e-=i*c*(d>100?d/10:10)}return e},roundValue(t){if(!this.stepNumeric)return t;const e=this.step.toString().trim(),i=e.indexOf(".")>-1?e.length-e.indexOf(".")-1:0,s=this.minValue%this.stepNumeric,a=Math.round((t-s)/this.stepNumeric)*this.stepNumeric+s;return parseFloat(Math.min(a,this.maxValue).toFixed(i))}}})},ec29:function(t,e,i){},fe09:function(t,e,i){"use strict";var s=i("c37a"),a=i("5311"),n=i("8547"),l=i("58df");e["a"]=Object(l["a"])(s["a"],a["a"],n["a"]).extend({name:"selectable",model:{prop:"inputValue",event:"change"},props:{id:String,inputValue:null,falseValue:null,trueValue:null,multiple:{type:Boolean,default:null},label:String},data(){return{hasColor:this.inputValue,lazyValue:this.inputValue}},computed:{computedColor(){if(this.isActive)return this.color?this.color:this.isDark&&!this.appIsDark?"white":"accent"},isMultiple(){return!0===this.multiple||null===this.multiple&&Array.isArray(this.internalValue)},isActive(){const t=this.value,e=this.internalValue;return this.isMultiple?!!Array.isArray(e)&&e.some(e=>this.valueComparator(e,t)):void 0===this.trueValue||void 0===this.falseValue?t?this.valueComparator(t,e):Boolean(e):this.valueComparator(e,this.trueValue)},isDirty(){return this.isActive}},watch:{inputValue(t){this.lazyValue=t,this.hasColor=t}},methods:{genLabel(){const t=s["a"].options.methods.genLabel.call(this);return t?(t.data.on={click:t=>{t.preventDefault(),this.onChange()}},t):t},genInput(t,e){return this.$createElement("input",{attrs:Object.assign({"aria-checked":this.isActive.toString(),disabled:this.isDisabled,id:this.computedId,role:t,type:t},e),domProps:{value:this.value,checked:this.isActive},on:{blur:this.onBlur,change:this.onChange,focus:this.onFocus,keydown:this.onKeydown},ref:"input"})},onBlur(){this.isFocused=!1},onChange(){if(this.isDisabled)return;const t=this.value;let e=this.internalValue;if(this.isMultiple){Array.isArray(e)||(e=[]);const i=e.length;e=e.filter(e=>!this.valueComparator(e,t)),e.length===i&&e.push(t)}else e=void 0!==this.trueValue&&void 0!==this.falseValue?this.valueComparator(e,this.trueValue)?this.falseValue:this.trueValue:t?this.valueComparator(e,t)?null:t:!e;this.validate(!0,e),this.internalValue=e,this.hasColor=e},onFocus(){this.isFocused=!0},onKeydown(t){}}})}}]);
//# sourceMappingURL=chunk-5af10b37.cf8850a1.js.map