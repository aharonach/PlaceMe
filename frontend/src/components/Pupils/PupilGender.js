import {toCapitalCase} from "../../utils";
import Gender from "../General/Gender";
import React from "react";

export default function PupilGender({ pupil }) {
    return <Gender gender={pupil.gender} noIcon pill>{toCapitalCase(pupil.gender)}</Gender>;
}