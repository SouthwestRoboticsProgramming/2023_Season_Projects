(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["chunk-01759076"],{1447:function(t,e,i){"use strict";var s=function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",[i("v-row",{attrs:{dense:"",align:"center"}},[i("v-col",{attrs:{cols:2}},[i("span",[t._v(t._s(t.name))])]),i("v-col",{attrs:{cols:10}},[i("v-slider",{staticClass:"align-center",attrs:{value:t.localValue,dark:"",max:t.max,min:t.min,"hide-details":"",color:"#4baf62",step:t.step},on:{start:function(e){t.isClicked=!0},end:function(e){t.isClicked=!1},change:t.handleclick,input:t.handleInput},scopedSlots:t._u([{key:"append",fn:function(){return[i("v-text-field",{staticClass:"mt-0 pt-0",staticStyle:{width:"50px"},attrs:{dark:"",max:t.max,min:t.min,value:t.localValue,"hide-details":"","single-line":"",type:"number",step:t.step},on:{input:t.handleChange,focus:function(e){t.isFocused=!0},blur:function(e){t.isFocused=!1}}})]},proxy:!0}])})],1)],1)],1)},a=[],l={name:"Slider",props:["min","max","name","value","step"],data(){return{isFocused:!1,isClicked:!1}},methods:{handleChange(t){this.isFocused&&(this.localValue=parseFloat(t))},handleInput(t){!this.isFocused&&this.isClicked&&(this.localValue=t)},handleclick(t){this.isFocused||(this.localValue=t)}},computed:{localValue:{get(){return this.value},set(t){this.$emit("input",t)}}}},n=l,r=i("2877"),h=i("6544"),o=i.n(h),u=i("62ad"),c=i("0fd9"),d=i("ba0d"),m=i("8654"),p=Object(r["a"])(n,s,a,!1,null,"3505e445",null);e["a"]=p.exports;o()(p,{VCol:u["a"],VRow:c["a"],VSlider:d["a"],VTextField:m["a"]})},8384:function(t,e,i){"use strict";var s=function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",[i("v-row",{attrs:{dense:"",align:"center"}},[i("v-col",{attrs:{cols:3}},[i("span",[t._v(t._s(t.name))])]),i("v-col",{attrs:{cols:9}},[i("v-select",{attrs:{items:t.indexList,"item-text":"name","item-value":"index",dark:"",color:"#4baf62","item-color":"green",disabled:t.disabled},model:{value:t.localValue,callback:function(e){t.localValue=e},expression:"localValue"}})],1)],1)],1)},a=[],l={name:"Select",props:["list","name","value","disabled"],data(){return{}},computed:{localValue:{get(){return this.value},set(t){this.$emit("input",t)}},indexList(){let t=[];for(let e=0;e<this.list.length;e++)t.push({name:this.list[e],index:e});return t}}},n=l,r=i("2877"),h=i("6544"),o=i.n(h),u=i("62ad"),c=i("0fd9"),d=i("b974"),m=Object(r["a"])(n,s,a,!1,null,null,null);e["a"]=m.exports;o()(m,{VCol:u["a"],VRow:c["a"],VSelect:d["a"]})},"9e29":function(t,e,i){},ba0d:function(t,e,i){"use strict";i("9e29");var s=i("c37a"),a=i("0789"),l=i("58df"),n=i("297c"),r=i("a293"),h=i("80d2"),o=i("d9bd");e["a"]=Object(l["a"])(s["a"],n["a"]).extend({name:"v-slider",directives:{ClickOutside:r["a"]},mixins:[n["a"]],props:{disabled:Boolean,inverseLabel:Boolean,max:{type:[Number,String],default:100},min:{type:[Number,String],default:0},step:{type:[Number,String],default:1},thumbColor:String,thumbLabel:{type:[Boolean,String],default:void 0,validator:t=>"boolean"===typeof t||"always"===t},thumbSize:{type:[Number,String],default:32},tickLabels:{type:Array,default:()=>[]},ticks:{type:[Boolean,String],default:!1,validator:t=>"boolean"===typeof t||"always"===t},tickSize:{type:[Number,String],default:2},trackColor:String,trackFillColor:String,value:[Number,String],vertical:Boolean},data:()=>({app:null,oldValue:null,keyPressed:0,isFocused:!1,isActive:!1,noClick:!1}),computed:{classes(){return{...s["a"].options.computed.classes.call(this),"v-input__slider":!0,"v-input__slider--vertical":this.vertical,"v-input__slider--inverse-label":this.inverseLabel}},internalValue:{get(){return this.lazyValue},set(t){t=isNaN(t)?this.minValue:t;const e=this.roundValue(Math.min(Math.max(t,this.minValue),this.maxValue));e!==this.lazyValue&&(this.lazyValue=e,this.$emit("input",e))}},trackTransition(){return this.keyPressed>=2?"none":""},minValue(){return parseFloat(this.min)},maxValue(){return parseFloat(this.max)},stepNumeric(){return this.step>0?parseFloat(this.step):0},inputWidth(){const t=(this.roundValue(this.internalValue)-this.minValue)/(this.maxValue-this.minValue)*100;return t},trackFillStyles(){const t=this.vertical?"bottom":"left",e=this.vertical?"top":"right",i=this.vertical?"height":"width",s=this.$vuetify.rtl?"auto":"0",a=this.$vuetify.rtl?"0":"auto",l=this.disabled?`calc(${this.inputWidth}% - 10px)`:`${this.inputWidth}%`;return{transition:this.trackTransition,[t]:s,[e]:a,[i]:l}},trackStyles(){const t=this.vertical?this.$vuetify.rtl?"bottom":"top":this.$vuetify.rtl?"left":"right",e=this.vertical?"height":"width",i="0px",s=this.disabled?`calc(${100-this.inputWidth}% - 10px)`:`calc(${100-this.inputWidth}%)`;return{transition:this.trackTransition,[t]:i,[e]:s}},showTicks(){return this.tickLabels.length>0||!(this.disabled||!this.stepNumeric||!this.ticks)},numTicks(){return Math.ceil((this.maxValue-this.minValue)/this.stepNumeric)},showThumbLabel(){return!this.disabled&&!(!this.thumbLabel&&!this.$scopedSlots["thumb-label"])},computedTrackColor(){if(!this.disabled)return this.trackColor?this.trackColor:this.isDark?this.validationState:this.validationState||"primary lighten-3"},computedTrackFillColor(){if(!this.disabled)return this.trackFillColor?this.trackFillColor:this.validationState||this.computedColor},computedThumbColor(){return this.thumbColor?this.thumbColor:this.validationState||this.computedColor}},watch:{min(t){const e=parseFloat(t);e>this.internalValue&&this.$emit("input",e)},max(t){const e=parseFloat(t);e<this.internalValue&&this.$emit("input",e)},value:{handler(t){this.internalValue=t}}},beforeMount(){this.internalValue=this.value},mounted(){this.app=document.querySelector("[data-app]")||Object(o["c"])("Missing v-app or a non-body wrapping element with the [data-app] attribute",this)},methods:{genDefaultSlot(){const t=[this.genLabel()],e=this.genSlider();return this.inverseLabel?t.unshift(e):t.push(e),t.push(this.genProgress()),t},genSlider(){return this.$createElement("div",{class:{"v-slider":!0,"v-slider--horizontal":!this.vertical,"v-slider--vertical":this.vertical,"v-slider--focused":this.isFocused,"v-slider--active":this.isActive,"v-slider--disabled":this.disabled,"v-slider--readonly":this.readonly,...this.themeClasses},directives:[{name:"click-outside",value:this.onBlur}],on:{click:this.onSliderClick}},this.genChildren())},genChildren(){return[this.genInput(),this.genTrackContainer(),this.genSteps(),this.genThumbContainer(this.internalValue,this.inputWidth,this.isActive,this.isFocused,this.onThumbMouseDown,this.onFocus,this.onBlur)]},genInput(){return this.$createElement("input",{attrs:{value:this.internalValue,id:this.computedId,disabled:this.disabled,readonly:!0,tabindex:-1,...this.$attrs}})},genTrackContainer(){const t=[this.$createElement("div",this.setBackgroundColor(this.computedTrackColor,{staticClass:"v-slider__track-background",style:this.trackStyles})),this.$createElement("div",this.setBackgroundColor(this.computedTrackFillColor,{staticClass:"v-slider__track-fill",style:this.trackFillStyles}))];return this.$createElement("div",{staticClass:"v-slider__track-container",ref:"track"},t)},genSteps(){if(!this.step||!this.showTicks)return null;const t=parseFloat(this.tickSize),e=Object(h["g"])(this.numTicks+1),i=this.vertical?"bottom":"left",s=this.vertical?"right":"top";this.vertical&&e.reverse();const a=e.map(e=>{const a=this.$vuetify.rtl?this.maxValue-e:e,l=[];this.tickLabels[a]&&l.push(this.$createElement("div",{staticClass:"v-slider__tick-label"},this.tickLabels[a]));const n=e*(100/this.numTicks),r=this.$vuetify.rtl?100-this.inputWidth<n:n<this.inputWidth;return this.$createElement("span",{key:e,staticClass:"v-slider__tick",class:{"v-slider__tick--filled":r},style:{width:`${t}px`,height:`${t}px`,[i]:`calc(${n}% - ${t/2}px)`,[s]:`calc(50% - ${t/2}px)`}},l)});return this.$createElement("div",{staticClass:"v-slider__ticks-container",class:{"v-slider__ticks-container--always-show":"always"===this.ticks||this.tickLabels.length>0}},a)},genThumbContainer(t,e,i,s,a,l,n,r="thumb"){const h=[this.genThumb()],o=this.genThumbLabelContent(t);return this.showThumbLabel&&h.push(this.genThumbLabel(o)),this.$createElement("div",this.setTextColor(this.computedThumbColor,{ref:r,staticClass:"v-slider__thumb-container",class:{"v-slider__thumb-container--active":i,"v-slider__thumb-container--focused":s,"v-slider__thumb-container--show-label":this.showThumbLabel},style:this.getThumbContainerStyles(e),attrs:{role:"slider",tabindex:this.disabled||this.readonly?-1:this.$attrs.tabindex?this.$attrs.tabindex:0,"aria-label":this.label,"aria-valuemin":this.min,"aria-valuemax":this.max,"aria-valuenow":this.internalValue,"aria-readonly":String(this.readonly),"aria-orientation":this.vertical?"vertical":"horizontal",...this.$attrs},on:{focus:l,blur:n,keydown:this.onKeyDown,keyup:this.onKeyUp,touchstart:a,mousedown:a}}),h)},genThumbLabelContent(t){return this.$scopedSlots["thumb-label"]?this.$scopedSlots["thumb-label"]({value:t}):[this.$createElement("span",[String(t)])]},genThumbLabel(t){const e=Object(h["f"])(this.thumbSize),i=this.vertical?`translateY(20%) translateY(${Number(this.thumbSize)/3-1}px) translateX(55%) rotate(135deg)`:"translateY(-20%) translateY(-12px) translateX(-50%) rotate(45deg)";return this.$createElement(a["e"],{props:{origin:"bottom center"}},[this.$createElement("div",{staticClass:"v-slider__thumb-label-container",directives:[{name:"show",value:this.isFocused||this.isActive||"always"===this.thumbLabel}]},[this.$createElement("div",this.setBackgroundColor(this.computedThumbColor,{staticClass:"v-slider__thumb-label",style:{height:e,width:e,transform:i}}),[this.$createElement("div",t)])])])},genThumb(){return this.$createElement("div",this.setBackgroundColor(this.computedThumbColor,{staticClass:"v-slider__thumb"}))},getThumbContainerStyles(t){const e=this.vertical?"top":"left";let i=this.$vuetify.rtl?100-t:t;return i=this.vertical?100-i:i,{transition:this.trackTransition,[e]:`${i}%`}},onThumbMouseDown(t){this.oldValue=this.internalValue,this.keyPressed=2,this.isActive=!0;const e=!h["w"]||{passive:!0,capture:!0},i=!!h["w"]&&{passive:!0};"touches"in t?(this.app.addEventListener("touchmove",this.onMouseMove,i),Object(h["a"])(this.app,"touchend",this.onSliderMouseUp,e)):(this.app.addEventListener("mousemove",this.onMouseMove,i),Object(h["a"])(this.app,"mouseup",this.onSliderMouseUp,e)),this.$emit("start",this.internalValue)},onSliderMouseUp(t){t.stopPropagation(),this.keyPressed=0;const e=!!h["w"]&&{passive:!0};this.app.removeEventListener("touchmove",this.onMouseMove,e),this.app.removeEventListener("mousemove",this.onMouseMove,e),this.$emit("end",this.internalValue),Object(h["i"])(this.oldValue,this.internalValue)||(this.$emit("change",this.internalValue),this.noClick=!0),this.isActive=!1},onMouseMove(t){const{value:e}=this.parseMouseMove(t);this.internalValue=e},onKeyDown(t){if(this.disabled||this.readonly)return;const e=this.parseKeyDown(t,this.internalValue);null!=e&&(this.internalValue=e,this.$emit("change",e))},onKeyUp(){this.keyPressed=0},onSliderClick(t){if(this.noClick)return void(this.noClick=!1);const e=this.$refs.thumb;e.focus(),this.onMouseMove(t),this.$emit("change",this.internalValue)},onBlur(t){this.isFocused=!1,this.$emit("blur",t)},onFocus(t){this.isFocused=!0,this.$emit("focus",t)},parseMouseMove(t){const e=this.vertical?"top":"left",i=this.vertical?"height":"width",s=this.vertical?"clientY":"clientX",{[e]:a,[i]:l}=this.$refs.track.getBoundingClientRect(),n="touches"in t?t.touches[0][s]:t[s];let r=Math.min(Math.max((n-a)/l,0),1)||0;this.vertical&&(r=1-r),this.$vuetify.rtl&&(r=1-r);const h=n>=a&&n<=a+l,o=parseFloat(this.min)+r*(this.maxValue-this.minValue);return{value:o,isInsideTrack:h}},parseKeyDown(t,e){if(this.disabled)return;const{pageup:i,pagedown:s,end:a,home:l,left:n,right:r,down:o,up:u}=h["s"];if(![i,s,a,l,n,r,o,u].includes(t.keyCode))return;t.preventDefault();const c=this.stepNumeric||1,d=(this.maxValue-this.minValue)/c;if([n,r,o,u].includes(t.keyCode)){this.keyPressed+=1;const i=this.$vuetify.rtl?[n,u]:[r,u],s=i.includes(t.keyCode)?1:-1,a=t.shiftKey?3:t.ctrlKey?2:1;e+=s*c*a}else if(t.keyCode===l)e=this.minValue;else if(t.keyCode===a)e=this.maxValue;else{const i=t.keyCode===s?1:-1;e-=i*c*(d>100?d/10:10)}return e},roundValue(t){if(!this.stepNumeric)return t;const e=this.step.toString().trim(),i=e.indexOf(".")>-1?e.length-e.indexOf(".")-1:0,s=this.minValue%this.stepNumeric,a=Math.round((t-s)/this.stepNumeric)*this.stepNumeric+s;return parseFloat(Math.min(a,this.maxValue).toFixed(i))}}})}}]);
//# sourceMappingURL=chunk-01759076.5c267949.js.map