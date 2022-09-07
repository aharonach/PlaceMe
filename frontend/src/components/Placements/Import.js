import {useForm} from "react-hook-form";
import HtmlForm from "../Forms/HtmlForm";
import {BASE_URL, CSV_CONTENT_TYPE} from "../../api";
import useAxios from "../../hooks/useAxios";
import {useState} from "react";
import {Alert} from "react-bootstrap";

const fields = [
    {
        id: 'import_file',
        label: 'CSV File',
        type: 'file',
        rules: { required: true },
        bsProps: { accept: '.csv' }
    }
];

export default function Import({ placement }) {
    const form = useForm();
    const [response, error, loading, fetch] = useAxios();
    const [imported, setImported] = useState();
    const hasErrors = imported?.errorsCount > 0 && imported?.errors.length > 0;
    const columnsDownloadUrl = `${BASE_URL}placements/${placement.id}/export/columns`;

    const handleSubmit = async (data) => {
        setImported(null);
        const file = data.import_file[0];
        const fileContent = await file.arrayBuffer();
        const response = await fetch({
            url: `/placements/${placement.id}/import`,
            method: 'post',
            data: fileContent
        });

        if ( response ) {
            setImported(response);
        }
    };

    return (
        <>
            {error && <Alert variant="danger">{error}</Alert>}
            {imported && <Alert variant={hasErrors ? "info" : "success"}>
                <h5 className="mb-0">{!hasErrors ? "File imported successfully!" : `You have ${imported.errorsCount} errors in your file`}</h5>
                {hasErrors ? <ul className="mt-2">{imported.errors.map( error => <li>{error}</li>)}</ul> : ''}
            </Alert>}
            <h5>Step 1</h5>
            <p>Download the template for the import (or use an existing export as your template).</p>
            <p><a href={columnsDownloadUrl} download={CSV_CONTENT_TYPE}>Download Template</a></p>
            <hr />
            <h5>Step 2</h5>
            <p>Prepare your import file.</p>
            <h6>Instructions:</h6>
            <ul>
                <li>Attribute values should be between 1 to 5.</li>
                <li>Preferences are set by pupil's given ID.</li>
                <li>Multiple preferences should be separated by semicolon.</li>
            </ul>
            <hr />
            <h5>Step 3</h5>
            <p>Choose your file to upload and click "Import Now".</p>
            <HtmlForm formProps={form} fields={fields} submitLabel={"Import Now"} submitCallback={handleSubmit} loading={loading} />
        </>
    )
}