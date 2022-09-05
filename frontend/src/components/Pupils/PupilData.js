import {useOutletContext} from "react-router-dom";
import RecordDetails from "../RecordDetails";
import {humanizeTime} from "../../utils";
import PupilGender from "./PupilGender";
import React from "react";

export default function PupilData() {
    const { pupil } = useOutletContext();
    const details = [
        { label: "Given ID", value: pupil.givenId },
        { label: "First Name", value: pupil.firstName },
        { label: "Last Name", value: pupil.lastName },
        { label: "Gender", value: <PupilGender pupil={pupil} /> },
        { label: "Birth Date", value: `${humanizeTime(pupil.birthDate, 'DD/MM/YYYY')} (Age ${pupil.age})` },
        { label: "Created Time", value: humanizeTime(pupil.createdTime) },
    ];

    return <RecordDetails details={details} />
}