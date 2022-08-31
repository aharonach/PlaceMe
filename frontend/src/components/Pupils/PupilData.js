import {useOutletContext} from "react-router-dom";
import RecordDetails from "../RecordDetails";

export default function PupilData() {
    const { pupil } = useOutletContext();
    const details = [
        { label: "Given ID", value: pupil.givenId },
        { label: "First Name", value: pupil.firstName },
        { label: "Last Name", value: pupil.lastName },
        { label: "Gender", value: pupil.gender },
        { label: "Birth Date", value: pupil.birthDate },
        { label: "Created Time", value: pupil.createdTime },
    ];

    return <RecordDetails details={details} numOfColumns={3} />
}