import {useOutletContext} from "react-router-dom";
import RecordDetails from "../RecordDetails";
import {objectLinkList} from "../../utils";
import {Attributes} from "./index";

export default function TemplateData() {
    const { template } = useOutletContext();
    const details = [
        { label: "Name", value: template.name },
        { label: "Description", value: template.description },
        { label: "Number of Attributes", value: template.numberOfAttributes },
        { label: "Groups", value: objectLinkList('groups', template.groups, 'id') },
    ];

    return <>
        <RecordDetails details={details} numOfColumns={4} />
        <Attributes />
    </>
}