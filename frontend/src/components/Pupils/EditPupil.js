import React from "react";
import {useForm} from "react-hook-form";
import {useParams} from "react-router-dom";

export default function EditPupil() {
    let {register, handleSubmit, watch, formState: { errors }} = useForm();
    let { pupilId } = useParams();
    return (
        <p>Edit Pupil {pupilId}</p>
    );
}